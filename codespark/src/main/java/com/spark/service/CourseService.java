package com.spark.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.spark.Entity.AttendanceEntity;
import com.spark.Entity.SocialPaymentEntity;
import com.spark.Entity.SubjectReviewEntity;
import com.spark.controller.JoinController;
import com.spark.dto.AttendanceDTO;
import com.spark.dto.SocialPaymentDTO;
import com.spark.repository.AttendanceRepository;
import com.spark.repository.CourseRepository;
import com.spark.repository.SocialPaymentRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CourseService {

    private final JoinController joinController;

    @Autowired
    private CourseRepository courseRepo;

    @Autowired
    private AttendanceRepository attendRepo;
    
    @Autowired
    private SocialPaymentRepository PayRepo;

    CourseService(JoinController joinController) {
        this.joinController = joinController;
    }

	public ResponseEntity<?> getAllCourses() {
		List<Map<String, Object>>  CourseList = courseRepo.findAllClass();
		
	    return ResponseEntity.ok(CourseList);
	}

	public ResponseEntity<?> getCourses(String classId) {
		List<Map<String, Object>>  courseRawData  = courseRepo.ClassDetail(classId);
        Map<String, Object> courseDetailOrigin = courseRawData.get(0);
        Map<String, Object> courseDetail = new HashMap<>(courseDetailOrigin); 
		List<SubjectReviewEntity> reviews = courseRepo.findReview(classId);
		courseDetail.put("reviews", reviews);
	    return ResponseEntity.ok(courseDetail);
	}

	public ResponseEntity<Boolean> PaymentEnd(String class_id, String login_id) {
		
		return null;
	}

	public void savePaymentInfo(SocialPaymentDTO payment) {
		SocialPaymentEntity entity =  new SocialPaymentEntity(payment);
		System.out.println(entity);
		PayRepo.save(entity);
		
	}

	public void saveAttendInfo(AttendanceDTO attDto) {
		AttendanceEntity entity =  new AttendanceEntity(attDto);
		attendRepo.save(entity);
	}

	public Integer getPaymentPK(String payment_code) {
		SocialPaymentEntity payment=PayRepo.findByPaymentCode(payment_code);

		return payment.getPaymentId();
	}



}
