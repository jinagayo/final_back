package com.spark.service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import com.spark.Entity.UserEntity;
import com.spark.controller.AuthController.ApiResponse;
import com.spark.dto.UserDTO;
import com.spark.repository.AuthRepository;
import com.spark.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;


/*로그인 로그아웃 서비스*/
@Service
public class AuthService {

	@Autowired
	private AuthRepository AuthRepo;
	
	@Autowired
	private PasswordEncoder pwEncoder;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private  VerificationCodeService verificationCodeService;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private CustomUserDetailsService userDetailsService; // ✅ 소문자 시작
	public ResponseEntity<?> authenticateUser(UserDTO login, HttpServletRequest request) {
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
			
			// 🔧 Spring Security 인증 객체 등록 (핵심 추가 부분)
			UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserId());
			UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			SecurityContext context = SecurityContextHolder.getContext();
			context.setAuthentication(authentication);
			
			// 2) 세션에 저장  ❗❗
			HttpSession session = request.getSession(true);          // 세션 없으면 새로 만듦
			session.setAttribute(
			        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
			        context);
			
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

	public ApiResponse sendVerificationCode(String userId, String email) {
		  Optional<UserEntity> userOpt = userRepo.findByUserIdAndEmail(userId, email);
	        if (userOpt.isEmpty()) {
	            return new ApiResponse(false, "일치하는 사용자가 없습니다.");
	        }
	        // 6자리 랜덤 코드 생성
	        String code = String.format("%06d", (int)(Math.random() * 1000000));
	        verificationCodeService.saveCode(userId, code);

	        // 메일 전송
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setTo(email);
	        message.setSubject("[Codespark] 비밀번호 재설정 인증코드");
	        message.setText("인증코드: " + code + "\n10분 이내에 입력해주세요.");
	        mailSender.send(message);

	        return new ApiResponse(true, "인증 코드가 이메일로 전송되었습니다.");
	}

	public ApiResponse sendTempPassword(String userId, String email, String verificationCode) {
		Optional<UserEntity> userOpt = userRepo.findByUserIdAndEmail(userId, email);
		if (userOpt.isEmpty()) {
            return new ApiResponse(false, "일치하는 사용자가 없습니다.");
        }
        if (!verificationCodeService.verifyCode(userId, verificationCode)) {
            return new ApiResponse(false, "인증코드가 올바르지 않습니다.");
        }
        
        //임시비밀번호 생성
        String tempPassword =  generateTempPassword(8);
        UserEntity user = userOpt.get();
        user.setPw(pwEncoder.encode(tempPassword));
        userRepo.save(user);
        verificationCodeService.removeCode(userId);
        
        //임시 비밀번호 메일 발송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[Codespark] 임시 비밀번호 안내");
        message.setText("임시 비밀번호: " + tempPassword + "\n로그인 후 반드시 비밀번호를 변경해주세요.");
        mailSender.send(message);
        
        return new ApiResponse(true, "임시 비밀번호가 이메일로 발송되었습니다.");
	}

    // 임시 비밀번호 생성 유틸
    private String generateTempPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

}
