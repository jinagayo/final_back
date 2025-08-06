package com.spark.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spark.dto.ClassInfoDTO;
import com.spark.dto.MeterialDTO;
import com.spark.dto.MeterialSubDTO;
import com.spark.dto.SubjectReviewDTO;
import com.spark.dto.TestDTO;
import com.spark.dto.TestSubDTO;
import com.spark.Entity.MeterialEntity;
import com.spark.Entity.MeterialSubEntity;
import com.spark.Entity.SubjectReviewEntity;
import com.spark.Entity.TestEntity;
import com.spark.Entity.TestSubEntity;
import com.spark.repository.AttendanceRepository;
import com.spark.repository.BoardRepository;
import com.spark.repository.CourseRepository;
import com.spark.repository.MeterialRepository;
import com.spark.repository.MeterialSubRepository;
import com.spark.repository.SubjectReviewRepository;
import com.spark.repository.TestRepository;
import com.spark.repository.TestSubRepository;

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
    private MeterialSubRepository meterialSubRepo;
    @Autowired
    private AttRepository attRepo;
    @Autowired
    private TestRepository testRepo;
    @Autowired
    private TestSubRepository testSubRepo;
    

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
		  if (data == null) return null;
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
		if (attId == null) return false;  // null이면 리뷰 없음 취급
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


	public MeterialEntity testmaterial(MeterialDTO dto) {
		MeterialEntity entity = new MeterialEntity(dto);
		return meterialRepo.save(entity);
	}


	public TestEntity testquestions(TestEntity entity) {
		return testRepo.save(entity);
	}


	public void materialsDelete(MeterialEntity entity) {
		 meterialRepo.delete(entity);;
	}


	public MeterialEntity MeterialDeleteSeq(String classId) {
		List<MeterialEntity> list = meterialRepo.findByClassId(Integer.parseInt(classId));
		int i=1;
		for(MeterialEntity entity :list) {
			entity.setSeq(i++);
			meterialRepo.save(entity);
		}
		return null;
	}


	public void materialsReorder(MeterialDTO dto) {
		meterialRepo.changeSeq(dto.getMeterId(),dto.getSeq());
		
	}


	public List<MeterialSubEntity> getMeterialSub(Integer MetId) {
		
		return meterialSubRepo.findByMeterialId(MetId);
	}


	public List<TestEntity> getTest(String meterialId) {
		return testRepo.findByMeterialId( Integer.parseInt(meterialId));
	}


	public void testSubmit(String id, TestSubDTO dto) {
		dto.setStudId(id);
		Optional<TestEntity> test = testRepo.findById(dto.getTestnum());
		String answer = test.get().getAnswer();
		if(answer.equals(dto.getSubmit())||answer==dto.getSubmit()) {
			dto.setCorrect(true);
		}else {
			dto.setCorrect(false);
		}
		TestSubEntity entity = new TestSubEntity(dto);
		testSubRepo.save(entity);
	}


	public void materialTestDone(String meterialId, String id, double score) {
		MeterialSubDTO dto = new MeterialSubDTO();
		dto.setMeterialId(Integer.parseInt(meterialId));
		dto.setStdId(id);
		dto.setContent(String.format("%.2f", score));
		MeterialSubEntity entity = new MeterialSubEntity(dto);
		meterialSubRepo.save(entity);
		
		
	}


	public Boolean testYN(String meterialId,String id) {
		List<MeterialSubEntity> entity = meterialSubRepo.testYN(meterialId,id);
		if(entity.isEmpty()) return false;
		else return true;
	}


	public List<MeterialSubEntity> getMeterialSubOne(String meterialId, String id) {
		List<MeterialSubEntity> entity = meterialSubRepo.testYN(meterialId,id);
		return entity;
	}


	public TestSubEntity getTestSub(String meterialId, String id) {
		List<TestSubEntity> data= testSubRepo.findSubmit(meterialId,id);
		return data.get(0);
	}


	public Optional<MeterialSubEntity> getMeterialSubOne(String meteriaSublId) {
		// TODO Auto-generated method stub
		return meterialSubRepo.findById(Integer.parseInt(meteriaSublId));
	}


	public TestSubEntity getTestSub(String meteriaSublId) {
		// TODO Auto-generated method stub
		return null;
	}




	public List<MeterialDTO> getLecturesWithProgress(int classId, String studentId) {
		//1. 전체 강의 목록 조회
		List<MeterialEntity> lectures = meterialRepo.findByClassId(classId);
		
		//2. 강의 id 리스트 뽑기
		List<Integer> meterIds = lectures.stream()
				.map(MeterialEntity::getMeterId)
				.collect(Collectors.toList());
		
		// 엔티티 -> DTO 변환
		List<MeterialDTO> lecturesDTO = lectures.stream()
				.map(entity -> {
					MeterialDTO dto = new MeterialDTO();
					BeanUtils.copyProperties(entity, dto);
					return dto;
				})
				.collect(Collectors.toList());
		
		//3. 해당 학생의 각 강의별 progress를 Map으로 한번에 조회
		List<Object[]> progressList = meterialSubRepo.findProgressByStudentAndMeterIds(studentId, meterIds);
		Map<Integer, Integer> progressMap = new HashMap<>();
		for(Object[] row : progressList) {
		    Integer meterId = ((Number) row[0]).intValue();
		    Integer progress = ((Number) row[1]).intValue();
		    progressMap.put(meterId, progress);
		}
		
		//4. lectures에 progress 값 주입
		for(MeterialDTO dto : lecturesDTO) {
			int progress = progressMap.getOrDefault(dto.getMeterId(), 0);
			dto.setProgress(progress);
		}
		
		return lecturesDTO;
	}



	public Object studentDidIt(List<MeterialSubEntity> sub, MeterialEntity m) {
		System.out.println("왜 있냐고"+sub);
		if(sub==null||sub.isEmpty()) {
			return false;
		}else { //sub가 있는 경우
			if(m.getType().equals("MET001")) {//동영상인 경우
				if(sub.get(0).getProgress()>=80) { //80 이상 들었을때
					return true;
				}else return false; //80 이하
			}else return true; //동영상이 아닌 경우
		}
		
	}

}
