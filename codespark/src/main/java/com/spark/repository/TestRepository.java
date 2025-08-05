package com.spark.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spark.Entity.MeterialEntity;
import com.spark.Entity.TestEntity;
@Repository
public interface TestRepository extends JpaRepository<TestEntity, Integer> {

	List<TestEntity> findByMeterialId(int int1);

}
