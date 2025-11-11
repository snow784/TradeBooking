package com.example.fraud_model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fraud_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudResultEntity {

    @Id
    @Column(name = "trade_id", nullable = false, unique = true)
    private String tradeId;
    @Column(name = "is_fraud", nullable = false)
    private String isFraud;
}
