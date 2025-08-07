package com.spark.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spark.Entity.AttendanceEntity;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Integer>{


	@Query("SELECT a.stuId FROM AttendanceEntity a WHERE a.classId = :classId")
	List<String> findStudentIdsByClassId(@Param("classId") int classId);
	

	@Query(value = "SELECT att_id from attendance where stu_id=:id and class_id=:classId",
	    nativeQuery = true)
	Integer findAttId(@Param("id")String id, @Param("classId")String classId);
	
	@Query("SELECT COUNT(a) FROM AttendanceEntity a WHERE a.classId = :classId")
	Integer countByClassId(@Param("classId") int i);

	@Modifying
	@Query(value = "DELETE FROM attendance WHERE class_id = :classId", nativeQuery = true)
	void deleteByclassId(@Param("classId") Integer id);





}
