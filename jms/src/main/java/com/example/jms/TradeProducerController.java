package com.example.jms;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TradeProducerController {

    private final TradeProducer tradeProducer;

    public TradeProducerController(TradeProducer tradeProducer) {
        this.tradeProducer = tradeProducer;
    }

    @PostMapping("publishTrade")
    public ResponseEntity<String> publishTrade(@RequestBody TradeDTO tradeContent) {
        tradeProducer.sendTradeMessage(tradeContent);
        return ResponseEntity.ok("Trade message published successfully");
    }

}
