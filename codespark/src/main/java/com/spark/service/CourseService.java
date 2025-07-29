package com.spark.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.spark.Entity.AttendanceEntity;
import com.spark.Entity.ClassEntity;
import com.spark.Entity.SocialPaymentEntity;
import com.spark.Entity.SubjectReviewEntity;
import com.spark.controller.JoinController;
import com.spark.dto.AttendanceDTO;
import com.spark.dto.ClassDTO;
import com.spark.dto.ClassInfoDTO;
import com.spark.dto.SocialPaymentDTO;
import com.spark.repository.AttendanceRepository;
import com.spark.repository.CommonRepository;
import com.spark.repository.CourseRepository;
import com.spark.repository.SocialPaymentRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CourseService {

    @Autowired
    private CourseRepository courseRepo;

    @Autowired
    private AttendanceRepository attendRepo;
    
    @Autowired
    private SocialPaymentRepository PayRepo;

    @Autowired
    private CommonRepository CommRepo;

	public ResponseEntity<?> getAllCourses() {
		List<ClassInfoDTO>  CourseList = courseRepo.findAllClass();
		
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


	public List<Map<String,Object>> getCategories(String string) {
		List<Map<String,Object>> data= CommRepo.findCom(string+"%");
		return data;
	}

	public List<ClassInfoDTO> getClassList(String id) {
		List<ClassInfoDTO> ClassList =courseRepo.findByTeachId(id);
		return ClassList;
	}

	public ClassEntity teacherApplication(ClassDTO submit) {
		ClassEntity entity = new ClassEntity(submit);
		return courseRepo.save(entity);
		
	}

	public List<ClassInfoDTO> findAllRequest() {
		// TODO Auto-generated method stub
		return courseRepo.findAllRequest();
	}

	public void requestSolve(String id, String classId, String action) {
		courseRepo.adminRequestSolve(id,classId,action);
		
	}

	public List<Map<String, Object>>  findClass(String classId) {
		List<Map<String, Object>>  data = courseRepo.ClassDetail(classId);
		return data;
	}

	public void deleteClass(String classId) {
		Integer id = Integer.parseInt(classId);
		courseRepo.deleteById(id);
		
	}




}
