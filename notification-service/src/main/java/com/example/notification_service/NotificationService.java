package com.example.notification_service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class NotificationService {

    @Autowired
    private ResultDao resultDao;

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // per-trade lock store
    private final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();

    public NotificationService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    private Object lockOf(String tradeId) {
        return locks.computeIfAbsent(tradeId, t -> new Object());
    }

    private String normalizeId(String id) {
        return id == null ? null : id.trim();   // pick a canonical form
    }

    private String normalizeFlag(String f) {
        return f == null ? null : f.trim().toLowerCase();     // "yes"/"no"
    }

    // ---------------- RULE RESULT ------------------------
    @JmsListener(destination = "rule_result.queue", containerFactory = "jmsListenerContainerFactory")
    public void processRuleResult(String message) {
        System.out.println("Processing rule result: " + message);
        try {
            RuleResultDTO rule = objectMapper.readValue(message, RuleResultDTO.class);
            String tradeId = normalizeId(rule.getTradeId());
            String ruleFlag = normalizeFlag(rule.getIsFailed());

            if (tradeId == null) {
                System.err.println("Null tradeId in rule result, skipping");
                return;
            }

            Object lock = lockOf(tradeId);
            synchronized (lock) {
                // Upsert: set rule value, leave fraud unchanged if null
                resultDao.upsert(tradeId, ruleFlag, null);

                // read latest and evaluate
                Optional<ResultEntity> opt = resultDao.findById(tradeId);
                opt.ifPresent(this::evaluate);
            }

        } catch (Exception e) {
            // log and rethrow? We handle with JMS ErrorHandler in config — keep simple
            e.printStackTrace();
        }
    }

    // ---------------- FRAUD RESULT ------------------------
    @JmsListener(destination = "fraud_result.queue", containerFactory = "jmsListenerContainerFactory")
    public void processFraudResult(String message) {
        System.out.println("Processing fraud result: " + message);
        try {
            FraudResultDTO fraud = objectMapper.readValue(message, FraudResultDTO.class);
            String tradeId = normalizeId(fraud.getTradeId());
            String fraudFlag = normalizeFlag(fraud.getIsFraud());

            if (tradeId == null) {
                System.err.println("Null tradeId in fraud result, skipping");
                return;
            }

            Object lock = lockOf(tradeId);
            synchronized (lock) {
                // Upsert: set fraud value, leave rule unchanged if null
                resultDao.upsert(tradeId, null, fraudFlag);

                Optional<ResultEntity> opt = resultDao.findById(tradeId);
                opt.ifPresent(this::evaluate);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- COMMON RESULT EVALUATION -------------------
    private void evaluate(ResultEntity r) {

        String rule = normalizeFlag(r.getRuleIsFailed());
        String fraud = normalizeFlag(r.getFraudIsFailed());
        String tradeId = r.getTradeId();

        if (rule == null || fraud == null) {
            System.out.println("Waiting for both results...");
            return;
        }

        if ("yes".equals(rule) || "yes".equals(fraud)) {
            System.out.println("Trade " + tradeId + " FAILED → NACK");
            jmsTemplate.convertAndSend("notification.queue", tradeId + " NACK");
        } else {
            System.out.println("Trade " + tradeId + " SUCCESS → ACK");
            jmsTemplate.convertAndSend("notification.queue", tradeId + " ACK");
        }
    }
}
