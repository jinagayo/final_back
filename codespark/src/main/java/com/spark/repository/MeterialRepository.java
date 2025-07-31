package com.spark.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spark.Entity.MeterialEntity;


public interface MeterialRepository extends JpaRepository<MeterialEntity, Integer>{

	//classId 와 type 모두 조건
	List<MeterialEntity> findByClassIdAndType(Integer classId, String string);

	@Query("SELECT COALESCE(MAX(m.seq), 0) FROM MeterialEntity m WHERE m.classId = :classId")
	int findNextSeqByClassId(@Param("classId") Integer classId);

	 List<MeterialEntity> findByClassId(Integer classId);
  
	MeterialEntity findByMeterId(Integer id);


	

}
