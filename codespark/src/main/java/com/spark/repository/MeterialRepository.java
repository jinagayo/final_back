package com.spark.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spark.Entity.MeterialEntity;

@Repository
public interface MeterialRepository extends JpaRepository<MeterialEntity, Integer>  {

	List<MeterialEntity> findByClassId(String classId);

}
