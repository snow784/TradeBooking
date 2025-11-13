package com.example.notification_service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import jakarta.transaction.Transactional;


public interface ResultDao extends JpaRepository<ResultEntity, String>{
/**
     * Upsert using MySQL ON DUPLICATE KEY UPDATE.
     * This is idempotent — safe even if message processed multiple times.
     */
    @Modifying
    @Transactional
    @Query(
      value = "INSERT INTO results (trade_id, rule_is_failed, fraud_is_failed) " +
              "VALUES (:tradeId, :rule, :fraud) " +
              "ON DUPLICATE KEY UPDATE " +
              " rule_is_failed = COALESCE(VALUES(rule_is_failed), rule_is_failed), " +
              " fraud_is_failed = COALESCE(VALUES(fraud_is_failed), fraud_is_failed)",
      nativeQuery = true)
    void upsert(String tradeId, String rule, String fraud);
}
