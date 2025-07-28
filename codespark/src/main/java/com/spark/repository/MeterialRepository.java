package com.spark.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spark.Entity.MeterialEntity;
import com.spark.dto.MeterialDTO;


public interface MeterialRepository extends JpaRepository<MeterialEntity, Integer>{

	//classId 와 type 모두 조건
	List<MeterialEntity> findByClassIdAndType(String classId, String string);

	@Query("SELECT COALESCE(MAX(m.seq), 0) FROM MeterialEntity m WHERE m.classId = :classId")
	int findNextSeqByClassId(@Param("classId") String classId);

	MeterialEntity findByMeterId(Integer id);
}
