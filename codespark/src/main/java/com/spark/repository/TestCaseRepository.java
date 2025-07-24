package com.spark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spark.Entity.CodingEntity;

@Repository
public interface TestCaseRepository extends JpaRepository<CodingEntity, Integer> {

}
