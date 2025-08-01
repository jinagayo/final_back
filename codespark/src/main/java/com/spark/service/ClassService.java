package com.spark.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spark.dto.ClassInfoDTO;
import com.spark.dto.MeterialDTO;
import com.spark.dto.SubjectReviewDTO;
import com.spark.Entity.MeterialEntity;
import com.spark.Entity.SubjectReviewEntity;
import com.spark.repository.AttendanceRepository;
import com.spark.repository.BoardRepository;
import com.spark.repository.CourseRepository;
import com.spark.repository.MeterialRepository;
import com.spark.repository.SubjectReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.spark.Entity.ClassEntity;
import com.spark.Entity.MeterialEntity;
import com.spark.controller.JoinController;
import com.spark.dto.ClassDTO;
import com.spark.dto.MeterialDTO;
import com.spark.repository.AttRepository;
import com.spark.repository.CourseRepository;
import com.spark.repository.JoinRepository;
import com.spark.repository.LectureRepository;
import com.spark.repository.MeterialRepository;
import com.spark.repository.QnaRepository;
import com.spark.repository.UserRepository;


@Service
@Transactional
public class ClassService {
    @Autowired
    private SubjectReviewRepository subjectReviewRepo;
    @Autowired
    private CourseRepository courseRepo;
    @Autowired
    private QnaRepository qnaRepo;
    @Autowired
    private LectureRepository lectureRepo;
    @Autowired
    private MeterialRepository meterialRepo;
    @Autowired
    private AttRepository attRepo;
    
    ClassService(CourseRepository courseRepo) {
        this.courseRepo = courseRepo;
    }
    

	public List<Map<String, Object>> getAllClass(String id) {
		List<Map<String, Object>> data = courseRepo.getMyClass(id);
		System.out.println("서비스 동작" +data);
		return data;
	}
	public ClassInfoDTO getClass(String class_id) {
		ClassInfoDTO data = courseRepo.findClassInfo(class_id);
		data.setQnaCount(qnaRepo.countByClassId(Integer.parseInt(class_id)));
		data.setLectureCount(lectureRepo.countByClassId(Integer.parseInt(class_id)));
		data.setStudentCount(attRepo.countByClassId(Integer.parseInt(class_id)));
		return data;
	}
	public List<MeterialEntity> getMeterials(Integer classId) {
		List<MeterialEntity> data = meterialRepo.findByClassId(classId);
		return data;
	}
	public Integer getAttId(String stuId, String classId) {
		return attRepo.findAttId(stuId,classId);
	}
	public Boolean reviewYN(Integer attId) {
		List<SubjectReviewEntity> entity = subjectReviewRepo.findByAttId(attId);
		System.out.println("================================\n"+entity);
		if(entity.isEmpty()) {
			return false;
		}
		return true;
	}


    public ClassDTO getClassDetail(Integer class_id) {
    	//1. 강의(클래스) 엔티티 한 건 가져옴
    	ClassEntity classEntity = courseRepo.findById(class_id).orElseThrow();
    	
    	//2. 해당 강의(클래스)에 소속된 강의 개수 집계
    	int lectureCount = lectureRepo.countByClassId(class_id);
    	
    	//3. 해당 강의(클래스)에 소속된 Q&A 개수 집계
    	int qnaCount = qnaRepo.countByClassId(class_id);
    	
    	//4. 해당 강의(클래스)를 수강하는 학생 후 집계
    	int studentCount = attRepo.countByClassId(class_id);
    	
    	//4. DTO(응답 객체) 생성 및 값 세팅
    	ClassDTO dto = new ClassDTO();
    	dto.setTeachId(classEntity.getTeachId());
    	dto.setDetail(classEntity.getDetail());
    	dto.setSubId(classEntity.getSubId());
    	dto.setPrice(classEntity.getPrice());
    	dto.setImg(classEntity.getImg());
    	dto.setIntro(classEntity.getIntro());
    	dto.setName(classEntity.getName());
    	return dto;
    }
    

	// 강사별 강의+검색+페이징
	public Page<ClassDTO> getCoursesByTeacher(String class_id, String search, Pageable pageable) {
	    Page<ClassEntity> result;
	    if(search != null && !search.trim().isEmpty()) {
	        result = courseRepo.findByTeachIdAndNameContainingIgnoreCase(class_id, search, pageable);
	    } else {
	        result = courseRepo.findByTeachId(class_id, pageable);
	    }

	    // **이 map 내부에서 직접 set 해줘야 함**
	    return result.map(entity -> {
	        ClassDTO dto = ClassDTO.fromEntity(entity);
	        return dto;
	    });
	}

	public List<MeterialDTO> getLectures(Integer classId) {
		// 엔티티로 바로 받는 경우
		List<MeterialEntity> meterials = meterialRepo.findByClassIdAndType(classId, "MET001");
		// Entity -> DTO 변환
		return meterials.stream()
				.map(MeterialDTO::fromEntity)
				.toList();
	}


	public void saveReview(SubjectReviewDTO dto) {
		SubjectReviewEntity entity = new SubjectReviewEntity(dto);
		subjectReviewRepo.save(entity);
		
	}


	public List<SubjectReviewEntity> getAllReview(String classId) {
		// TODO Auto-generated method stub
		return subjectReviewRepo.findByClassId(classId);
	}


	public MeterialEntity TeacherassignmentForm(MeterialDTO dto) {
		MeterialEntity entity = new MeterialEntity(dto);
		MeterialEntity data = meterialRepo.save(entity);
		return data;
	}


	public MeterialEntity getMeterialOne(String meterialId) {
		MeterialEntity data = meterialRepo.findByMeterId(Integer.parseInt(meterialId));
		return data;
	}


}
