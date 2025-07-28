package com.spark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spark.Entity.SocialPaymentEntity;

@Repository
public interface SocialPaymentRepository extends JpaRepository<SocialPaymentEntity, Integer>{
	SocialPaymentEntity findByPaymentCode(String payment_code);
}
