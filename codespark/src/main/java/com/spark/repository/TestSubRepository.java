package com.spark.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spark.Entity.TestSubEntity;


@Repository
public interface TestSubRepository  extends JpaRepository<TestSubEntity, Integer> {

	 @Query("SELECT ts FROM TestSubEntity ts WHERE ts.testnum = :meterialId AND ts.studId = :id")
	List<TestSubEntity> findSubmit(@Param("meterialId")String meterialId, @Param("id") String id);

}
