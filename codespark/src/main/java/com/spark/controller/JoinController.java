package com.spark.controller;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.spark.dto.UserDTO;
import com.spark.service.JoinService;
import com.spark.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/join")
@CrossOrigin(origins="http://localhost:3000", allowCredentials="true")
@EnableWebMvc
public class JoinController {
    
    @Autowired
    private JoinService joinService;
    
    @Autowired
    private UserService userService;
    
    // 학생 회원가입
    @PostMapping("/signup/student")
    @ResponseBody 
    public ResponseEntity<?> signupStudent(@RequestBody UserDTO request) {
    	return joinService.createStudent(request);
    }
    
    // 강사 회원가입
    @PostMapping("/signup/teacher") 
    public ResponseEntity<?> signupInstructor(@RequestBody UserDTO request) {
        return joinService.createInstructor(request);
    }
    
    // 아이디 중복 검사
    @GetMapping("/check-userid/{userId}")
    public ResponseEntity<?> checkUserId(@PathVariable String userId) {
        System.out.println("아이디 중복 검사: " + userId);
        return joinService.checkUserIdAvailability(userId);
    }
    
    // 이메일 중복 검사
    @GetMapping("/check-email/{email}")
    public ResponseEntity<?> checkEmail(@PathVariable String email) {
        System.out.println("이메일 중복 검사: " + email);
        return joinService.checkEmailAvailability(email);
    }
    
    // 테스트용 엔드포인트
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("API 연결 테스트 성공!");
    }
    
    //로그인
    @PostMapping("/login")
    public String loginUser(@RequestBody UserDTO request) {
        return "login";
    }
    //회원 탈퇴
    @PostMapping("/userDelete/{userId}")
    public ResponseEntity<?> deleteUser(
            @PathVariable String userId,
            @RequestBody Map<String, String> request,
            HttpSession session) {
        System.out.println("userDelete 동작");
        String inputPassword = request.get("password");
        String id = (String)session.getAttribute("login");
        
        if(!userService.passWordCheck(id,inputPassword)) {
        	return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "비밀번호가 일치하지 않습니다."));
        	
        }else {
        	userService.deleteUser(userId);
        	return ResponseEntity.ok("회원 삭제 완료");
        }
    	
    }
}
