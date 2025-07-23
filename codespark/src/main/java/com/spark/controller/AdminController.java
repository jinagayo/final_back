package com.spark.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import java.io.File;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.spark.Entity.UserEntity;
import com.spark.repository.UserRepository;
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

            response.put("success", true);
            response.put("data", teacherPage.getContent()); // 강사 페이징 데이터
            response.put("currentPage", page);
            response.put("totalPages", teacherPage.getTotalPages());
            response.put("totalElements", teacherPage.getTotalElements());
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
    
	// 관리자 권한 확인
    private boolean isAdmin(HttpSession session) {
        String userPosition = (String) session.getAttribute("position");
        return "3".equals(userPosition);
    }
}