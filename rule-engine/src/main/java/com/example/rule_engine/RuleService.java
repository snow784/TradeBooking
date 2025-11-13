package com.example.rule_engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@Component
public class RuleService {

    @Autowired
    private RuleResultDao ruleResultDao;
    
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    public RuleService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
        this.objectMapper = new ObjectMapper();

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
            jmsTemplate.convertAndSend("rule_result.queue", message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
