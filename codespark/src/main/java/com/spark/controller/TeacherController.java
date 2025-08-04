package com.spark.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spark.Entity.ClassEntity;
import com.spark.Entity.MeterialEntity;
import com.spark.Entity.MeterialSubEntity;
import com.spark.Entity.SubjectReviewEntity;
import com.spark.Entity.TestEntity;
import com.spark.Entity.UserEntity;
import com.spark.dto.ClassDTO;
import com.spark.dto.ClassInfoDTO;
import com.spark.dto.MeterialDTO;
import com.spark.dto.SubjectReviewDTO;
import com.spark.dto.TestDTO;
import com.spark.repository.MeterialRepository;
import com.spark.repository.UserRepository;
import com.spark.service.ClassService;
import com.spark.service.CourseService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/myclass/teacher")
@CrossOrigin(origins="http://localhost:3000", allowCredentials="true") // CORS 설정 추가
public class TeacherController {
	
	private final ClassService classService;
	

    @Autowired
    private MeterialRepository meterialRepo;
	@Autowired
	public TeacherController(ClassService classService) {
		this.classService = classService;
	}
	
	//강사의 강의 목록 조회 (페이지네이션 + 검색)
	@GetMapping("/classList")
	public ResponseEntity<?> getMyCourseList(
			@RequestParam(defaultValue = "1") int page,  //프론트에서 1부터 보낸다 가정
			@RequestParam(defaultValue = "10")int size,
			@RequestParam(required = false) String search,
			Principal principal   //현재 로그인한 유저(강사) 정보
	){
		// 로그인 강사 아이디(유니크) 추출
		String teacherId = principal.getName();
		
		
		  // PageRequest는 0-based이므로 -1
		  PageRequest pageRequest = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Direction.DESC,"classId"));
		  
		  // 서비스에서 강사의 강의 목록을 조회 (검색 포함)
		  Page<ClassDTO> pageResult = classService.getCoursesByTeacher(teacherId, search, pageRequest);
		  
		// 각 classDTO를 classInfoDTO로 변환하면서 카운트도 넣기 
			List<ClassInfoDTO> infoList = pageResult.getContent().stream().map(classDto -> {
				  ClassInfoDTO infoDto = new ClassInfoDTO();
				
		 // --- ClassDTO -> ClassInfoDTO 필드 복사
			BeanUtils.copyProperties(classDto, infoDto);  // 여기서 대부분 값 복사
			ClassInfoDTO dto = classService.getClass(""+classDto.getClassId());
			infoDto.setLectureCount(dto.getLectureCount());
			infoDto.setQnaCount(dto.getQnaCount());
				
			return infoDto;
			}).toList();
			
		  //응답 포맷 맞춰서 반환
		  Map<String, Object> response = new HashMap<>();
		  response.put("data", infoList);
		  response.put("currentPage", pageResult.getNumber() + 1);  //1-based
		  response.put("totalPages", pageResult.getTotalPages());
		  response.put("totalElements", pageResult.getTotalElements());
		  
		  return ResponseEntity.ok(response);
	}
	
	@GetMapping("/class/{classId}")
	public ResponseEntity<?> getClassDetail(@PathVariable String classId){
		 System.out.println("=== [API] classId: " + classId); 
		    ClassInfoDTO dto = classService.getClass(classId);
		    System.out.println("=== [API] classDTO.name: " + dto.getName());
		    return ResponseEntity.ok().body(dto);
	}

	@GetMapping("/class/{classId}/materials")
	public ResponseEntity<?> getClassmaterials(@PathVariable String classId){
		List<MeterialEntity> meterial = classService.getMeterials(Integer.parseInt(classId));
		    return ResponseEntity.ok().body(meterial);
	}
	@GetMapping("/class/{classId}/reviews")
	public ResponseEntity<?> getClassreviews(@PathVariable String classId){
		List<SubjectReviewEntity> meterial = classService.getAllReview(classId);
		    return ResponseEntity.ok().body(meterial);
	}
	@PostMapping("/assignmentForm")
	public ResponseEntity<?> assignmentForm(@RequestBody MeterialDTO dto){
		System.out.println("assignmentForm 동작");
		dto.setSeq(meterialRepo.findNextSeqByClassId(dto.getClassId())+1);
		System.out.println(dto);
		MeterialEntity data = classService.TeacherassignmentForm(dto);
		    return ResponseEntity.ok().body(data);
	}
	
	//클래스 강좌 목록
	@GetMapping("class/{classId}/lectures")
	public ResponseEntity<?> getLectures(
			@PathVariable String classId,
			@RequestParam("studentId") String studentId){
		List<MeterialDTO> lectures = classService.getLecturesWithProgress(Integer.parseInt(classId),studentId);
		return ResponseEntity.ok().body(Map.of("data",lectures));
	}
    @PostMapping("/testmaterial")
    public ResponseEntity<?> testmaterial(@RequestBody MeterialDTO dto) {
    	System.out.println("testmaterial 작동중");
		dto.setSeq(meterialRepo.findNextSeqByClassId(dto.getClassId())+1);
    	System.out.println(dto);
    	MeterialEntity data = classService.testmaterial(dto);
    	
        return ResponseEntity.ok().body(data);
    }
    @PostMapping("/testquestions")
    public ResponseEntity<?> testquestions(@RequestBody List<TestDTO> list) {
    	System.out.println("testquestions 작동중");
    	for(TestDTO dto : list) {
    		TestEntity entity = new TestEntity(dto);
    		TestEntity data =  classService.testquestions(entity);
    	}
        return ResponseEntity.ok("");
    }

    @PostMapping("/materials/delete")
    public ResponseEntity<?> materialsDelete(@RequestBody List<MeterialDTO> list,  @RequestParam("classId") String classId) {
    	System.out.println("materialsDelete 작동중");
    	for(MeterialDTO dto : list) {
    		System.out.println(dto);
    		MeterialEntity entity = new MeterialEntity(dto);
    		classService.materialsDelete(entity);
    	}
    	MeterialEntity aes = classService.MeterialDeleteSeq(classId);
    	
        return ResponseEntity.ok("");
    }
    @PostMapping("/materials/reorder")
    public ResponseEntity<?> materialsReorder(@RequestBody List<MeterialDTO> list,  @RequestParam("classId") String classId) {
    	System.out.println("materialsReorder 작동중");
    	for(MeterialDTO dto : list) {
    		System.out.println(dto);
    		classService.materialsReorder(dto);
    	}

        return ResponseEntity.ok("");
    }
    //과제물 리스트 불러오기
	@GetMapping("/material/submissions")
	public ResponseEntity<?> materialsubmissions(@RequestParam("meterial_id") String meterialId){
		System.out.println("materialsubmissions 작동중"+meterialId);
		List<MeterialSubEntity> list = classService.getMeterialSub(Integer.parseInt(meterialId));
		for(MeterialSubEntity l : list) System.out.println(l);
		return ResponseEntity.ok().body(list);
	}
}