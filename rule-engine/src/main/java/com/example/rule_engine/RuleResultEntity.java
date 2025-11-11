package com.example.rule_engine;

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
public class RuleResultEntity {

    @Id
    @Column(name = "trade_id", nullable = false, unique = true)
    private String tradeId;
    @Column(name = "is_failed", nullable = false)
    private String isFailed;
}
