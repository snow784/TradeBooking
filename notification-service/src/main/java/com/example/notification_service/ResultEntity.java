package com.example.notification_service;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultEntity {
    @Id
    @Column(name = "trade_id", nullable = false, unique = true)
    private String tradeId;

    @Column(name = "rule_is_failed")
    private String ruleIsFailed;

    @Column(name = "fraud_is_failed")
    private String fraudIsFailed;
}
