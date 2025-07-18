package com.spark.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spark.dto.UserDTO;
import com.spark.service.AuthService;

import jakarta.servlet.http.HttpSession;

//login,logout 관련 컨트롤러
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins="http://localhost:3000", allowCredentials="true")
public class AuthController {

    private final JoinController joinController;
	@Autowired
	private AuthService authService;

    AuthController(JoinController joinController) {
        this.joinController = joinController;
    }
	
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO login,HttpSession session) {
        try {
            System.out.println("=== 로그인 요청 ===");
            System.out.println("아이디: " + login.getUser_id());
            System.out.println("비밀번호: " + (login.getPw() != null ? "***있음***" : "NULL"));
            
            // 로그인 처리
            ResponseEntity<?> loginResult = authService.authenticateUser(login);
            
            //로그인 성공 시 세션 저장
            //응답코드가 200번대 인지 확인
            if(loginResult.getStatusCode().is2xxSuccessful()) {
            	Map<String, Object> responseBody = (Map<String, Object>) loginResult.getBody();
            	if((Boolean)responseBody.get("success")) {
            		session.setAttribute("login", login.getUser_id());
            		System.out.println("session 전달됨" + login.getUser_id());
            	}
            }
            return loginResult;
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "로그인 처리 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
  //session 확인
    @GetMapping("/check")
    public ResponseEntity<?> checkLoginStatus(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            //session 받음
            String login_id = (String)session.getAttribute("login");
            System.out.println("=== 세션 확인 ===");
            System.out.println("세션 ID: " + session.getId());
            System.out.println("저장된 login_id: " + login_id);
            
            if(login_id != null) {
                response.put("isLoggedIn", true);
                response.put("user_id", login_id);
                System.out.println("✅ 로그인 상태: " + login_id);
            } else {
                response.put("isLoggedIn", false);
                System.out.println("❌ 비로그인 상태");
            }
            
            return ResponseEntity.ok(response);
        } catch(Exception e) {
            e.printStackTrace();
            response.put("isLoggedIn", false);
            response.put("error", "세션 확인 중 오류발생");
            return ResponseEntity.ok(response);
        }
    }

    //로그아웃도 success 필드 추가
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        try {
            System.out.println("=== 로그아웃 요청 ===");
            System.out.println("기존 세션 ID: " + session.getId());
            
            // 세션 무효화
            session.invalidate();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);  // ← 추가!
            response.put("message", "로그아웃 되었습니다.");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "로그아웃 처리 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
