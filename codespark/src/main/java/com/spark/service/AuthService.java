package com.spark.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.spark.Entity.UserEntity;
import com.spark.dto.UserDTO;
import com.spark.repository.AuthRepository;

/*로그인 로그아웃 서비스*/
@Service
public class AuthService {

	@Autowired
	private AuthRepository AuthRepo;
	
	@Autowired
	private PasswordEncoder pwEncoder;
	public ResponseEntity<?> authenticateUser(UserDTO login) {
		try {
			//아이디 검증
			if(login.getUser_id() == null || login.getUser_id().trim().isEmpty()) {
				Map<String, Object> response = new HashMap<>();
				response.put("success", false);
				response.put("message", "아이디를 입력하세요");
				return ResponseEntity.badRequest().body(response);
			}
			//비밀번호 검증
			if(login.getPw() == null || login.getPw().trim().isEmpty()) {
				Map<String, Object> response = new HashMap<>();
				response.put("success", false);
				response.put("message", "비밀번호를 입력하세요");
				return ResponseEntity.badRequest().body(response);
			}
			//db에 있는 id와 login의 id를 비교
			Optional<UserEntity> userOptional = AuthRepo.findByUserId(login.getUser_id());
			

			if(!userOptional.isPresent()) { // 아이디가 없을 때
			    Map<String, Object> response = new HashMap<>();
			    response.put("success", false);
			    response.put("message", "존재하지 않은 아이디입니다.");
			    return ResponseEntity.badRequest().body(response);
			}
			
			//아이디 존재하면 user객체에 넣음
			UserEntity user = userOptional.get();
			
			//비밀번호 확인
			if(!pwEncoder.matches(login.getPw(), user.getPw())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "비밀번호가 일치하지 않습니다.");
                return ResponseEntity.badRequest().body(response);
			}
			
			//로그인 성공 콘솔 출력
			System.out.println("사용자: " + user.getName() + " (" + user.getUserId() + ")");
			System.out.println("권한: " + user.getPosition());
			
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "로그인에 성공했습니다!");
            response.put("user", Map.of(
                "userId", user.getUserId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "position", user.getPosition()  // 숫자 그대로 전송
            ));
            return ResponseEntity.ok(response);
			
		}catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "로그인 처리 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
		}
	}
	
	// 사용자 정보 조회 (세션 검증용)
    public ResponseEntity<?> getCurrentUser(String userId) {
        try {
            Optional<UserEntity> userOptional = AuthRepo.findByUserId(userId);
            
            if (!userOptional.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "사용자를 찾을 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            UserEntity user = userOptional.get();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("user", Map.of(
                "user_id", user.getUserId(),     // ✅ 일관성 유지
                "name", user.getName(),
                "email", user.getEmail(),
                "position", user.getPosition()
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "사용자 정보 조회 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

}
