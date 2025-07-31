package com.spark.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import java.io.File;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.spark.Entity.UserEntity;
import com.spark.controller.BoardController.ApiResponse;
import com.spark.dto.ApiResponseComment;
import com.spark.dto.ClassDTO;
import com.spark.dto.ClassInfoDTO;
import com.spark.repository.CourseRepository;
import com.spark.repository.UserRepository;
import com.spark.service.CourseService;
import com.spark.dto.CodingDTO;
import com.spark.service.CodingService;
import com.spark.service.UserService;

import jakarta.servlet.http.HttpSession;

// 관리자용 승인 컨트롤러
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins="http://localhost:3000", allowCredentials="true") // CORS 설정 추가
public class AdminController {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CourseService courseservice;

  @Autowired
	private CodingService coService;

	
	// 승인 대기중인 강사 목록조회
	@GetMapping("/pending-teachers")
	public ResponseEntity<?> getPendingTeachers(HttpSession session){
	    try {
	        if(!isAdmin(session)) {
	            Map<String, Object> errorResponse = new HashMap<>();
	            errorResponse.put("success", false);
	            errorResponse.put("message", "관리자 권한이 필요합니다.");
	            return ResponseEntity.status(403).body(errorResponse);
	        }
	        List<UserEntity> pendingTeachers = userRepo.findPendingTeachers();	        
	        Map<String, Object> response = new HashMap<>();
	        response.put("success", true);
	        response.put("data", pendingTeachers);
	        response.put("count", pendingTeachers.size());
	        
	        return ResponseEntity.ok(response);
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        Map<String, Object> errorResponse = new HashMap<>();
	        errorResponse.put("success", false);
	        errorResponse.put("message", "조회 중 오류가 발생했습니다: " + e.getMessage());
	        return ResponseEntity.internalServerError().body(errorResponse);
	    }
	}
	
	// 강사 승인
    @PostMapping("/approve-teacher/{userId}")
    public ResponseEntity<?> approveTeacher(@PathVariable String userId, HttpSession session) {
        try {            
            if (!isAdmin(session)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "관리자 권한이 필요합니다.");
                return ResponseEntity.status(403).body(errorResponse);
            }
            
            UserEntity user = userRepo.findByUserId(userId);
            
            if (user == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "사용자를 찾을 수 없습니다.");
                return ResponseEntity.badRequest().body(errorResponse);
            }
                        
            if (!"PENDING".equals(user.getState())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "승인 대기 상태가 아닙니다. 현재 상태: " + user.getState());
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // 승인 처리
            user.setPosition("2");      // 강사 권한 부여
            user.setState("ACTIVE");    // 활성 상태로 변경
            
            UserEntity savedUser = userRepo.save(user);
                        
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "강사 승인이 완료되었습니다.");
            response.put("user", savedUser);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "승인 처리 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
	
    // 강사 거부
    @PostMapping("/reject-teacher/{userId}")
    public ResponseEntity<?> rejectTeacher(@PathVariable String userId, 
                                         @RequestBody(required = false) Map<String, String> requestBody,
                                         HttpSession session) {
        try {            
            if (!isAdmin(session)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "관리자 권한이 필요합니다.");
                return ResponseEntity.status(403).body(errorResponse);
            }
            
            String rejectReason = requestBody != null ? requestBody.get("reason") : "사유 없음";
            
            UserEntity user = userRepo.findByUserId(userId);
            
            if (user == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "사용자를 찾을 수 없습니다.");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // 상태만 변경 (이력 보존)
            user.setState("REJECTED");
            UserEntity savedUser = userRepo.save(user);
            
            System.out.println("거부 완료 - 사용자: " + savedUser.getUserId() + 
                             ", 사유: " + rejectReason +
                             ", 새 상태: " + savedUser.getState());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "강사 신청이 거부되었습니다.");
            response.put("reason", rejectReason);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "거부 처리 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    
    //배너 이미지 업로드
    @PostMapping("/upload-banner")
    public ResponseEntity<?> uploadBanner(@RequestParam("file") MultipartFile file, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 관리자 권한 확인
            String position = (String) session.getAttribute("position");
            if (!"3".equals(position) && !"admin".equals(position)) {
                response.put("success", false);
                response.put("message", "관리자 권한이 필요합니다.");
                return ResponseEntity.status(403).body(response);
            }
            
            // 파일 검증
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "파일이 선택되지 않았습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 이미지 파일 확인
            String contentType = file.getContentType();
            if (!contentType.startsWith("image/")) {
                response.put("success", false);
                response.put("message", "이미지 파일만 업로드 가능합니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 파일 저장 경로 설정
            String uploadDir = "D:/codesparkReact/final_front/public/img/";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            // 기존 main.png 백업 (선택사항)
            File existingFile = new File(uploadDir + "main.png");
            if (existingFile.exists()) {
                File backupFile = new File(uploadDir + "main_backup_" + System.currentTimeMillis() + ".png");
                existingFile.renameTo(backupFile);
            }
            
            // 새 파일 저장
            File newFile = new File(uploadDir + "main.png");
            file.transferTo(newFile);
            
            response.put("success", true);
            response.put("message", "배너 이미지가 성공적으로 업로드되었습니다.");
            response.put("imagePath", "/img/main.png");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "파일 업로드 중 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(response);
        }
    }
    	
    
 // 강사 목록 조회
    @GetMapping("/teachers")
    public ResponseEntity<?> getTeachers(HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search) {
        
        Map<String, Object> response = new HashMap<>();

        try {
            // 관리자 권한 확인
            String position = (String) session.getAttribute("position");
            if (!"3".equals(position) && !"admin".equals(position)) {
                response.put("success", false);
                response.put("message", "관리자 권한이 필요합니다.");
                return ResponseEntity.status(403).body(response);
            }

            // 페이징 계산
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<UserEntity> teacherPage = userService.getTeachersPaginated(pageable, search); // 강사용 메소드 사용
            Page<UserEntity> teacherWait = userService.getZeroPositionUsers(pageable, search);//승인중인 강사
            
            System.out.println("teacherWait" + teacherWait);
            
            response.put("success", true);
            response.put("data", teacherPage.getContent()); // 강사 페이징 데이터
            response.put("currentPage", page);
            response.put("totalPages", teacherPage.getTotalPages());
            response.put("totalElements", teacherWait.getTotalElements());
            response.put("pendingTotal", teacherWait.getTotalElements()); // 승인 대기 강사 수
            response.put("size", size);
            response.put("hasNext", teacherPage.hasNext());
            response.put("hasPrevious", teacherPage.hasPrevious());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "강사 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // 학생 목록 조회
    @GetMapping("/students")
    public ResponseEntity<?> getStudents(
            HttpSession session,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search) {  // 기본값 "" 추가
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 관리자 권한 확인
            String position = (String) session.getAttribute("position");
            if (!"3".equals(position) && !"admin".equals(position)) {
                response.put("success", false);
                response.put("message", "관리자 권한이 필요합니다.");
                return ResponseEntity.status(403).body(response);
            }
            
            // 페이징 계산
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<UserEntity> studentPage = userService.getStudentsPaginated(pageable, search);

            response.put("success", true);
            response.put("data", studentPage.getContent());  // 페이징된 데이터만
            response.put("currentPage", page);
            response.put("totalPages", studentPage.getTotalPages());
            response.put("totalElements", studentPage.getTotalElements());
            response.put("size", size);
            response.put("hasNext", studentPage.hasNext());
            response.put("hasPrevious", studentPage.hasPrevious());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "학생 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    
    
    //코딩문제 업로드
    @PostMapping("/problem-upload")
    public ResponseEntity<?> createProblem(@RequestBody CodingDTO codingDTO, HttpSession session) {
        try {
            Map<String, Object> result = coService.createProblemWithValidation(codingDTO, session);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                int statusCode = (Integer) result.getOrDefault("statusCode", 400);
                return ResponseEntity.status(statusCode).body(result);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "문제 등록 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    //코딩 문제 조회
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getCodingList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "latest") String sortBy,
            @RequestParam(defaultValue = "all") String level) {
    	return coService.getCodingListWithPaging(page,size,search,sortBy,level);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Map<String, Object>> getCodingDetail(@PathVariable int id) {
    	try {
            System.out.println("=== 문제 상세 조회 API 호출 ===");
            System.out.println("요청된 문제 ID: " + id);
            
            // 서비스에서 문제 상세 정보 조회
            CodingDTO problemData = coService.getCodingDetail(id);
            
            if (problemData == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "문제를 찾을 수 없습니다.");
                errorResponse.put("code", "NOT_FOUND");
                return ResponseEntity.status(404).body(errorResponse);
            }
            
            // 성공 응답
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", problemData);
            response.put("timestamp", System.currentTimeMillis());
            
            System.out.println("문제 상세 조회 성공: " + problemData.getTitle());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("문제 상세 조회 오류: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "서버 내부 오류가 발생했습니다.");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
	// 관리자 권한 확인
    private boolean isAdmin(HttpSession session) {
        String userPosition = (String) session.getAttribute("position");
        return "3".equals(userPosition);
    }


    // 강의 리스트 조회
    @GetMapping("/class/ClassList")
    public ResponseEntity<?> rejectTeacher() {
    	System.out.println("ClassList 컨트롤러 작동");
    	List<ClassInfoDTO> data = courseservice.findAllRequest();
    	for(ClassInfoDTO d : data) System.out.println(d);
        return ResponseEntity.ok().body(data);
    
    }
    //강의 요청 수락/거절
    @PostMapping("/class/Request/{classId}/{action}")
    public ResponseEntity<?> Request(@PathVariable(name="classId") String classId,@PathVariable(name="action") String action,HttpSession session ) {
    	System.out.println("Request 컨트롤러 작동");
    	String id = (String)session.getAttribute("login");
    	courseservice.requestSolve(id,classId,action);
    	
    	
        return ResponseEntity.ok("");
    	
    
    }

    // 강의 세부정보 조회
    @GetMapping("/class/Detail/{classId}")
    public ResponseEntity<?> ClassDetail(@PathVariable(name="classId") String classId) {
    	System.out.println("ClassDetail 컨트롤러 작동");
    	List<Map<String, Object>>  data = courseservice.findClass(classId);
    	for(Map<String, Object> l :data) System.err.println(l);
        return ResponseEntity.ok().body(data);
    
    }
    // 강의 삭제
    @DeleteMapping("/class/Delete/{classId}")
    public ResponseEntity<?> ClassDelete(@PathVariable(name="classId") String classId) {
    	System.out.println("Delete 컨트롤러 작동");
    	courseservice.deleteClass(classId);
        return ResponseEntity.ok("");
    
    }
    
}