package com.spark.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spark.Entity.SubjectReviewEntity;

@Repository
public interface SubjectReviewRepository extends JpaRepository<SubjectReviewEntity, Integer> {

	List<SubjectReviewEntity> findByAttId(Integer attId);

}
