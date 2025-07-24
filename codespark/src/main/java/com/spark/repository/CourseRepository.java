package com.spark.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spark.Entity.ClassEntity;
import com.spark.dto.ClassDTO;

@Repository
public interface CourseRepository extends JpaRepository<ClassEntity, String> {

	@Query(value = "SELECT c.class_id as classId, c.name as name, c.detail, c.price, c.intro, c.mark, c.img, u.name as teacher, com.name as subject " +
            "FROM `class` c JOIN `user` u ON c.teach_id = u.user_id JOIN `common` com ON c.sub_id = com.com_id",
	    nativeQuery = true)
	List<Map<String, Object>> findAllClass();
	
	//기본
	Page<ClassEntity> findByTeachId(String teachId, Pageable pageable);
	
	//검색
	Page<ClassEntity> findByTeachIdAndNameContainingIgnoreCase(String teachId, String name, Pageable pageable);


}
