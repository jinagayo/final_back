package com.spark.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.spark.Entity.ClassEntity;
import com.spark.controller.JoinController;
import com.spark.dto.ClassDTO;
import com.spark.repository.CourseRepository;
import com.spark.repository.JoinRepository;
import com.spark.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CourseService {

    private final JoinController joinController;
    
    @Autowired
    private CourseRepository courseRepo;

    CourseService(JoinController joinController) {
        this.joinController = joinController;
    }

	public ResponseEntity<?> getAllCourses() {
		List<Map<String, Object>>  CourseList = courseRepo.findAllClass();
		
	    return ResponseEntity.ok(CourseList);
	}



}
