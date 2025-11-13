package com.example.fraud_model;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeDTO implements Serializable{
    private String tradeId;
    
    private String timestamp;

    private String symbol;

    private String tradeType;

    private int quantity;

    private double price;

    private String broker;

    private String traderId;

    private String state;

    private String exchange;
}
