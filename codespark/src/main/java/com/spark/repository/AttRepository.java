package com.spark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.spark.Entity.AttendanceEntity;
import com.spark.Entity.UserEntity;

@Repository
public interface AttRepository extends JpaRepository<AttendanceEntity, Integer>{
	@Query("SELECT COUNT(a) FROM AttendanceEntity a WHERE a.classId = :classId")
	int countByClassId(@Param("classId") Integer class_id);

}
