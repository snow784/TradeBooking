package com.example.fraud_model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@Component
public class FraudService {

    @Autowired
    private FraudResultDao fraudResultDao;

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    public FraudService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
        this.objectMapper = new ObjectMapper();

    }

    @Transactional
    @JmsListener(destination = "trade_fraud.queue")
    public void processTradeMessage(String message) {
        System.out.println("Processing trade message in Fraud detection model: " + message);
        try {
            TradeDTO trade = objectMapper.readValue(message, TradeDTO.class);
            FraudResultEntity fraudResult = new FraudResultEntity();
            fraudResult.setTradeId(trade.getTradeId());
            if(trade.getPrice()>9999){
                fraudResult.setIsFraud("yes");
            }else{
                fraudResult.setIsFraud("no");
            }
            fraudResultDao.save(fraudResult);
            sendResult(fraudResult);
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendResult(FraudResultEntity fraudResult){
        try {
            String message = objectMapper.writeValueAsString(fraudResult);
            jmsTemplate.convertAndSend("fraud_result.queue", message);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
    }
}