package com.example.jmsListener;

import org.springframework.beans.factory.annotation.Autowired;
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
        this.objectMapper = new ObjectMapper(); 

    }

    @Transactional
    public void consumeTrade(TradeEntity trade) {
        try {
            System.out.println("Received message: " + trade);
            trade.setState("CAPTURED");
            tradeDao.save(trade);
            System.out.println("Trade saved to database: " + trade);
            sendTradeToRuleQueue(trade);
            sendTradeToFraudQueue(trade);
            trade.setState("InProgress");
            tradeDao.save(trade);

        }catch(Exception e){
            e.printStackTrace();
        } 
    }

    public void sendTradeToRuleQueue(TradeEntity tradeContent){
        try {
            String trade = objectMapper.writeValueAsString(tradeContent);
            jmsTemplate.convertAndSend("trade", trade);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void sendTradeToFraudQueue(TradeEntity tradeContent){
        String trade;
        try {
            trade = objectMapper.writeValueAsString(tradeContent);
            jmsTemplate.convertAndSend("trade",trade);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
