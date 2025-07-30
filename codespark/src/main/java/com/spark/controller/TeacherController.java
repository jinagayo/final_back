package com.spark.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spark.Entity.ClassEntity;
import com.spark.Entity.UserEntity;
import com.spark.dto.ClassDTO;
import com.spark.dto.MeterialDTO;
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
		
		  //응답 포맷 맞춰서 반환
		  Map<String, Object> response = new HashMap<>();
		  response.put("data", pageResult.getContent());
		  response.put("currentPage", pageResult.getNumber() + 1);  //1-based
		  response.put("totalPages", pageResult.getTotalPages());
		  response.put("totalElements", pageResult.getTotalElements());
		  
		  return ResponseEntity.ok(response);
	}
	
	@GetMapping("/class/{classId}")
	public ResponseEntity<?> getClassDetail(@PathVariable String classId){
		 System.out.println("=== [API] classId: " + classId); 
		 	int id = Integer.parseInt(classId);  // 수동 파싱
		    ClassDTO dto = classService.getClassDetail(id);
		    System.out.println("=== [API] classDTO.Detail: " + dto.getDetail());
		    return ResponseEntity.ok().body(Map.of("data",dto));
	}
	
	//클래스 강좌 목록
	@GetMapping("class/{classId}/lectures")
	public ResponseEntity<?> getLectures(@PathVariable String classId){
		List<MeterialDTO> lectures = classService.getLectures(Integer.parseInt(classId));
		return ResponseEntity.ok().body(Map.of("data",lectures));
	}
}