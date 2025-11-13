package com.example.jmsListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

@Component
public class NotificationCapture {
    @Autowired
    TradeDao tradeDao;

    @Transactional
    @JmsListener(destination = "notification.queue", containerFactory = "jmsListenerContainerFactory")
    public void processRuleResult(String message) {
        System.out.println("Processing result in TradeCapture: " + message);
        try {

            String tradeId="";
            String status = message.substring(message.length() - 4).trim();
            if (status.equals("ACK")){
                tradeId = message.substring(0, message.length() - 4).trim();
            }else{
                tradeId = message.substring(0, message.length() - 5).trim();
            }
            TradeEntity trade = tradeDao.findById(tradeId).orElse(null);
            if (trade != null) {
                if (status.equals("ACK")) {
                    trade.setState("BOOKED");
                    System.out.println("Trade with ID " + tradeId + " is BOOKED.");
                } else if (status.equals("NACK")) {
                    trade.setState("FAILED");
                    System.out.println("Trade with ID " + tradeId + " has FAILED.");
                }
                tradeDao.save(trade);
            } else {
                System.out.println("Trade with ID " + tradeId + " not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }   
    }
}
