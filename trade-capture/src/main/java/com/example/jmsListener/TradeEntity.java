package com.example.jmsListener;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trades")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeEntity implements Serializable{

    @Id
    @Column(name = "trade_id", nullable = false, unique = true)
    private String tradeId;

    @Column(name = "timestamp", nullable = false)
    private String timestamp;

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Column(name = "trade_type", nullable = false)
    private String tradeType;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "broker", nullable = false)
    private String broker;

    @Column(name = "trader_id", nullable = false)
    private String traderId;

    @Column(name = "exchange", nullable = false)
    private String exchange;

    @Column(name = "state")
    private String state;
}
