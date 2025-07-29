package com.spark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spark.Entity.BoardEntity;

@Repository
public interface QnaRepository extends JpaRepository<BoardEntity, Integer>{

	@Query("SELECT COUNT(b) FROM BoardEntity b WHERE b.classId = :classId AND b.boardnum = 'BOD001'")
	int countByClassId(@Param("classId")int i);


}
