package com.spark.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spark.dto.ClassInfoDTO;
import com.spark.dto.MeterialDTO;
import com.spark.Entity.MeterialEntity;
import com.spark.Entity.SubjectReviewEntity;
import com.spark.repository.AttendanceRepository;
import com.spark.repository.BoardRepository;
import com.spark.repository.CourseRepository;
import com.spark.repository.MeterialRepository;
import com.spark.repository.SubjectReviewRepository;

@Service
@Transactional
public class ClassService {
    
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private MeterialRepository meterialRepository;
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private SubjectReviewRepository subjectReviewRepository;

	public List<Map<String, Object>> getAllClass(String id) {
		List<Map<String, Object>> data = courseRepository.getMyClass(id);
		System.out.println("서비스 동작" +data);
		return data;
	}
	public ClassInfoDTO getClass(String class_id) {
		ClassInfoDTO data = courseRepository.findClassInfo(class_id);
		return data;
	}
	public List<MeterialEntity> getMeterials(String classId) {
		List<MeterialEntity> data = meterialRepository.findByClassId(classId);
		return data;
	}
	public Integer getAttId(String id, String classId) {
		return attendanceRepository.findAttId(id,classId);
	}
	public Boolean reviewYN(Integer attId) {
		List<SubjectReviewEntity> entity = subjectReviewRepository.findByAttId(attId);
		if(entity==null) {
			return false;
		}
		return true;
	}
}
