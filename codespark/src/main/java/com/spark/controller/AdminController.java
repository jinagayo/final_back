package com.spark.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spark.Entity.UserEntity;
import com.spark.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

// 관리자용 승인 컨트롤러
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins="http://localhost:3000", allowCredentials="true") // CORS 설정 추가
public class AdminController {
	
	@Autowired
	private UserRepository userRepo;
	
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

	        System.out.println("강사 신청 대기자 수: " + pendingTeachers.size());
	        for (UserEntity teacher : pendingTeachers) {
	            System.out.println("강사 신청자: " + teacher.getUserId() + 
	                             ", 상태: " + teacher.getState() + 
	                             ", 권한: " + teacher.getPosition());
	        }
	        
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
            System.out.println("강사 승인 요청: " + userId);
            
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
            
            System.out.println("사용자 현재 상태: " + user.getState() + ", 권한: " + user.getPosition());
            
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
            
            System.out.println("승인 완료 - 사용자: " + savedUser.getUserId() + ", 새 상태: " + savedUser.getState() + ", 새 권한: " + savedUser.getPosition());
            
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
            System.out.println("강사 거부 요청: " + userId);
            
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
        	
	// 관리자 권한 확인
    private boolean isAdmin(HttpSession session) {
        String userPosition = (String) session.getAttribute("position");
        System.out.println("권한 체크 - userPosition: " + userPosition);
        return "3".equals(userPosition);
    }
}