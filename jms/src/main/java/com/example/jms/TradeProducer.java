package com.example.jms;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class TradeProducer {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    public TradeProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
        this.objectMapper = new ObjectMapper(); // Jackson mapper

    }
    public void sendTradeMessage(TradeDTO tradeContent){
        try {
            String jsonMessage = objectMapper.writeValueAsString(tradeContent);
            jmsTemplate.convertAndSend("trade.queue", jsonMessage);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
    }

    
}
