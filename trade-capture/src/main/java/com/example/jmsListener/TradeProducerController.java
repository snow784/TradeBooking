package com.example.jmsListener;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TradeProducerController {

    private final TradeCapture tradeCapture;

    public TradeProducerController(TradeCapture tradeCapture){
        this.tradeCapture = tradeCapture;
    }

    @PostMapping("publishTrade")
    public ResponseEntity<String> publishTrade(@RequestBody TradeEntity tradeContent) {
        tradeCapture.consumeTrade(tradeContent);
        return ResponseEntity.ok("Trade message published successfully");
    }
}
