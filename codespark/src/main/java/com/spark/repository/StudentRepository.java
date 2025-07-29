package com.spark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spark.Entity.StudentEntity;

@Repository
public interface StudentRepository  extends JpaRepository<StudentEntity, String>{

}
