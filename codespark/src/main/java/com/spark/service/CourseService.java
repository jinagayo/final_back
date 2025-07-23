package com.spark.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    CourseService(JoinController joinController, CourseRepository courseRepo) {
        this.joinController = joinController;
        this.courseRepo = courseRepo;
    }

	public ResponseEntity<?> getAllCourses() {
		List<Map<String, Object>>  CourseList = courseRepo.findAllClass();
		
	    return ResponseEntity.ok(CourseList);
	}


	// 강사별 강의+검색+페이징
	public Page<ClassEntity> getCoursesByTeacher(String teacherId, String search, Pageable pageable) {
		return null;
//		if(search != null && !search.trim().isEmpty()) {
//			return courseRepo
//					 .findByTeacherIdAndTitleContainingIgnoreCase(teacherId, search, pageable)
//					.map(ClassEntity::fromEntity);
//		} else {
//			return courseRepo
//					.findByTeacherId(teacherId, pageable)
//					.map(ClassEntity::fromEntity);
//		}
	}



}
