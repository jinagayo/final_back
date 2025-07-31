package com.spark.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spark.Entity.MeterialEntity;
import com.spark.Entity.TestEntity;

public interface TestRepository extends JpaRepository<TestEntity, Integer> {

}
