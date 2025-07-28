package com.spark.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spark.Entity.ClassEntity;
import com.spark.Entity.SubjectReviewEntity;
import com.spark.dto.ClassDTO;

@Repository
public interface CourseRepository extends JpaRepository<ClassEntity, Integer> {

	@Query(value = "SELECT c.class_id as classId, c.name as name, c.detail, c.price, c.intro, c.mark, c.img, u.name as teacher, com.name as subject " +
            "FROM `class` c JOIN `user` u ON c.teach_id = u.user_id JOIN `common` com ON c.sub_id = com.com_id",
	    nativeQuery = true)
	List<Map<String, Object>> findAllClass();

	@Query(value = "SELECT c.class_id as classId, c.name as name, c.detail, c.price, c.intro, c.mark, c.img, u.name as teacher, com.name as SUBJECT , t.career AS career, t.introduce AS introduce\r\n"
			+ "FROM `class` c\r\n"
			+ " JOIN `user` u ON c.teach_id = u.user_id "
			+ " JOIN `common` com ON c.sub_id = com.com_id "
			+ " JOIN `teacher` t ON c.teach_id = t.teach_id  where class_id= :classId",
	    nativeQuery = true)
	List<Map<String, Object>> ClassDetail(@Param("classId")String classId);

	@Query(value = "SELECT * FROM subject_review  WHERE class_id = :classId",nativeQuery = true)
	List<SubjectReviewEntity> findReview(@Param("classId") String classId);
	



}
