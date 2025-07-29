package com.spark.repository;


import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spark.Entity.BoardEntity;
import com.spark.Entity.MeterialEntity;

@Repository
public interface LectureRepository extends JpaRepository<MeterialEntity, Integer>{

	@Query("SELECT COUNT(m) FROM MeterialEntity m WHERE m.classId = :classId AND  m.type = 'MET001'")
	int countByClassId(@Param("classId") int i);

}