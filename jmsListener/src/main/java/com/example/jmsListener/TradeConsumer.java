package com.example.jmsListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@Component
public class TradeConsumer {
    @Autowired
    TradeDao tradeDao;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    @JmsListener(destination = "trade.queue")
    public void consumeMessage(String message) {
        try {
            TradeEntity trade = objectMapper.readValue(message, TradeEntity.class);
            System.out.println("Received message: " + trade);
            //sleep for 15 seconds to simulate processing time
            Thread.sleep(15000);
            tradeDao.save(trade);
            System.out.println("Trade saved to database: " + trade);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
