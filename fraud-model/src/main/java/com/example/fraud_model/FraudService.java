package com.example.fraud_model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@Component
public class FraudService {

    private final FraudResultDao fraudResultDao;
    private final JmsTemplate queueTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public FraudService(
            FraudResultDao fraudResultDao,
            @Qualifier("queueTemplate") JmsTemplate queueTemplate
    ) {
        this.fraudResultDao = fraudResultDao;
        this.queueTemplate = queueTemplate;
    }

    @Transactional
    @JmsListener(destination = "trade", subscription = "fraud" , containerFactory = "jmsListenerContainerFactory")
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
            queueTemplate.convertAndSend("fraud_result.queue", message);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
    }
}