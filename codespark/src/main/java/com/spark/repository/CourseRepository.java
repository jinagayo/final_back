package com.spark.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spark.Entity.ClassEntity;
import com.spark.Entity.SubjectReviewEntity;
import com.spark.dto.ClassDTO;
import com.spark.dto.ClassInfoDTO;

@Repository
public interface CourseRepository extends JpaRepository<ClassEntity, Integer> {

	@Query(value = "SELECT c.class_id as classId, c.name as name, c.detail as detail, c.price as price, c.intro as intro, c.mark as mark, c.img as img, u.name as teacher, "
			+ "com.name as subject, c.state as state, c.created_at as createdAt, c.updated_at as updatedAt,  c.created_by as createdBy, c.updated_by as updatedBy  " + 
            "FROM `class` c JOIN `user` u ON c.teach_id = u.user_id JOIN `common` com ON c.sub_id = com.com_id where c.state='STA001'",
	    nativeQuery = true)
	List<ClassInfoDTO> findAllClass();
	
	//강사의 강의 신청 목록 조회
	@Query(value = "SELECT c.class_id as classId, c.name as name, c.detail as detail, c.price as price, c.intro as intro, c.mark as mark, "
			+ "c.img as img, u.name as teacher, com.name as subject, c.state as state ,c.created_at as createdAt, c.updated_at as updatedAt, c.updated_by as updatedBy, c.created_by as createdBy " +
            "FROM `class` c JOIN `user` u ON c.teach_id = u.user_id JOIN `common` com ON c.sub_id = com.com_id where teach_id=:teachId",
	    nativeQuery = true)
	List<ClassInfoDTO> findByTeachId(@Param("teachId")String teachId);
	
	//검색
	Page<ClassEntity> findByTeachIdAndNameContainingIgnoreCase(String teachId, String name, Pageable pageable);

	@Query(value = "SELECT c.class_id as classId, c.name as name, c.detail, c.price, c.intro, c.mark, c.img, u.name as teacher, com.name as SUBJECT , t.career AS career, t.introduce AS introduce\r\n"
			+ "FROM `class` c\r\n"
			+ " JOIN `user` u ON c.teach_id = u.user_id "
			+ " JOIN `common` com ON c.sub_id = com.com_id "
			+ " JOIN `teacher` t ON c.teach_id = t.teach_id  where class_id= :classId",
	    nativeQuery = true)
	List<Map<String, Object>> ClassDetail(@Param("classId")String classId);

	@Query(value = "SELECT * FROM subject_review  WHERE class_id = :classId",nativeQuery = true)
	List<SubjectReviewEntity> findReview(@Param("classId") String classId);

	Optional<ClassEntity> findByClassId(Integer classId);

	Page<ClassEntity> findByTeachId(String classId, Pageable pageable);	



}
