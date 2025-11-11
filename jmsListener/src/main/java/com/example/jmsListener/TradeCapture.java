package com.example.jmsListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@Component
public class TradeCapture {
    @Autowired
    TradeDao tradeDao;

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    public TradeCapture(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
        this.objectMapper = new ObjectMapper(); // Jackson mapper

    }

    @Transactional
    @JmsListener(destination = "trade.queue")
    public void consumeMessage(String message) {
        try {
            TradeEntity trade = objectMapper.readValue(message, TradeEntity.class);
            System.out.println("Received message: " + trade);
            Thread.sleep(15000);
            trade.setState("CAPTURED");
            tradeDao.save(trade);
            System.out.println("Trade saved to database: " + trade);
            sendTradeToRuleQueue(message);
            sendTradeToFraudQueue(message);
            trade.setState("InProgress");
            tradeDao.save(trade);

        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendTradeToRuleQueue(String tradeContent){
        jmsTemplate.convertAndSend("trade_rule.queue", tradeContent);
    }
    public void sendTradeToFraudQueue(String tradeContent){
        jmsTemplate.convertAndSend("trade_fraud.queue",tradeContent);
    }
}
