package com.spark.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spark.Entity.AttendanceEntity;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Integer>{

	@Query("SELECT a.stuId FROM AttendanceEntity a WHERE a.classId = :classId")
	List<String> findStudentIdsByClassId(@Param("classId") int classId);
	

}
