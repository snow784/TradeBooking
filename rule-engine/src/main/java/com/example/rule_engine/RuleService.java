package com.example.rule_engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@Component
public class RuleService {

    private final RuleResultDao ruleResultDao;
    private final JmsTemplate queueTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public RuleService(
            RuleResultDao ruleResultDao,
            @Qualifier("queueTemplate") JmsTemplate queueTemplate
    ) {
        this.ruleResultDao = ruleResultDao;
        this.queueTemplate = queueTemplate;
    }

    @Transactional
    @JmsListener(destination = "trade", subscription="rule",containerFactory = "jmsListenerContainerFactory")
    public void processTradeMessage(String message) {
        System.out.println("Processing trade message in Rule Engine: " + message);
        try {
            TradeDTO trade = objectMapper.readValue(message,TradeDTO.class);
            RuleResultEntity ruleResult = new RuleResultEntity();
            ruleResult.setTradeId(trade.getTradeId());
            if(trade.getQuantity()>1000){
                ruleResult.setIsFailed("yes");
            }else{
                ruleResult.setIsFailed("no");
            }
            ruleResultDao.save(ruleResult);
            sendResult(ruleResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendResult(RuleResultEntity ruleResult){
        try {
            String message = objectMapper.writeValueAsString(ruleResult);
            queueTemplate.convertAndSend("rule_result.queue", message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
