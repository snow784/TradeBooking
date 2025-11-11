package com.example.fraud_model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FraudResultDao extends JpaRepository<FraudResultEntity, String>{

}
