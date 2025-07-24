package com.spark.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.spark.Entity.MeterialEntity;
import com.spark.Entity.UserEntity;

public interface VideoRepository extends JpaRepository<MeterialEntity, Integer> {
	Optional<MeterialEntity> findByMeterId(Integer meterId);
}
