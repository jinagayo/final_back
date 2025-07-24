package com.spark.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.spark.dto.UserDTO;
import com.spark.service.AuthService;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;

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
    public ResponseEntity<?> login(@RequestBody UserDTO login,
    				HttpServletRequest request, HttpSession session) {
        try {
            System.out.println("=== 로그인 요청 ===");
            System.out.println("아이디: " + login.getUser_id());
            System.out.println("비밀번호: " + (login.getPw() != null ? "***있음***" : "NULL"));
            
            // 로그인 처리
            ResponseEntity<?> loginResult = authService.authenticateUser(login,request);
            
            //로그인 성공 시 세션 저장
            //응답코드가 200번대 인지 확인
            if(loginResult.getStatusCode().is2xxSuccessful()) {
            	Map<String, Object> responseBody = (Map<String, Object>) loginResult.getBody();
            	if((Boolean)responseBody.get("success")) {
            		//세션 저장
            		session.setAttribute("login", login.getUser_id());
            		
            		Map<String, Object> user = (Map<String, Object>) responseBody.get("user");
            		if(user != null || user.get("position") != null) {
            			//권한을 session으로 저장
            			String position = (String)user.get("position");
            			String name = (String)user.get("name");
            			//문자열을 숫자로 변환
            			String positionNumber;
            			switch(position) {
            			case "admin": positionNumber = "3"; break;
            			case "teacher" : positionNumber = "2";break;
            			case "student" : positionNumber = "1";break;
            			default: positionNumber= position;
            			}
            			
            			session.setAttribute("position", positionNumber);
            			session.setAttribute("name", name);
            		}
            		
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
            // session 받음
            String login_id = (String)session.getAttribute("login");
            String position = (String)session.getAttribute("position");  // position도 가져오기
            String name = (String)session.getAttribute("name");
            
            System.out.println("=== 세션 확인 ===");
            System.out.println("세션 ID: " + session.getId());
            System.out.println("저장된 login_id: " + login_id);
            System.out.println("저장된 position: " + position);  // position 로그 추가

            if(login_id != null) {
                response.put("isLoggedIn", true);
                response.put("user_id", login_id);
                response.put("name", name);
                response.put("position", position);  // position 추가!
            } else {
                response.put("isLoggedIn", false);
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
    
    
    //네이버 로그인
    @PostMapping("/naver/callback")
    public ResponseEntity<?> naverCallback(@RequestBody Map<String, Object> callbackData, HttpSession session) {
        try {
            System.out.println("=== 네이버 콜백 처리 ===");
            
            String code = (String) callbackData.get("code");
            String state = (String) callbackData.get("state");
            
            if (code == null || code.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "인증 코드가 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 실제 네이버 API 호출
            String accessToken = getNaverAccessToken(code, state);
            Map<String, Object> naverUserInfo = getNaverUserInfo(accessToken);
            
            // 세션에 저장
            session.setAttribute("naverlogin", naverUserInfo.get("user_id"));
            session.setAttribute("LoginType", "naver");
            
            System.out.println("실제 네이버 사용자: " + naverUserInfo);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "네이버 로그인 성공");
            response.put("user", naverUserInfo);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "네이버 로그인 처리 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    //카카오 로그인
    @PostMapping("/kakao/callback")
    public ResponseEntity<?> kakaoCallback(@RequestBody Map<String, Object> callbackData, HttpSession session) {
        try {
            System.out.println("=== 카카오 콜백 처리 ===");
            System.out.println("받은 데이터: " + callbackData);
            
            String code = (String) callbackData.get("code");
            
            if (code == null || code.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "인증 코드가 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 실제 카카오 API 호출
            String accessToken = getKakaoAccessToken(code);
            Map<String, Object> kakaoUserInfo = getKakaoUserInfo(accessToken);
            
            // 세션에 저장
            session.setAttribute("kakaologin", kakaoUserInfo.get("user_id"));
            session.setAttribute("LoginType", "kakao");
            
            System.out.println("실제 카카오 사용자: " + kakaoUserInfo);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "카카오 로그인 성공");
            response.put("user", kakaoUserInfo);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "카카오 로그인 처리 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    
 // Access Token 받기
    private String getNaverAccessToken(String code, String state) {
        try {
            String clientId = "W24SNU9H24_ktuo8Bmmn";
            String clientSecret = "8A3x2kHkBI";
            String redirectUri = "http://localhost:3000/oauth";
            
            String url = "https://nid.naver.com/oauth2.0/token"
                    + "?grant_type=authorization_code"
                    + "&client_id=" + clientId
                    + "&client_secret=" + clientSecret
                    + "&code=" + code
                    + "&state=" + state;
            
            // HTTP 요청 보내기
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            Map<String, Object> responseBody = response.getBody();
            String accessToken = (String) responseBody.get("access_token");
            
            System.out.println("Access Token 받음: " + accessToken);
            return accessToken;
            
        } catch (Exception e) {
            throw new RuntimeException("Access Token 요청 실패: " + e.getMessage(), e);
        }
    }

    // 사용자 정보 받기
    private Map<String, Object> getNaverUserInfo(String accessToken) {
        try {
            String url = "https://openapi.naver.com/v1/nid/me";
            
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            
            HttpEntity<?> request = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            
            Map<String, Object> responseBody = response.getBody();
            Map<String, Object> naverResponse = (Map<String, Object>) responseBody.get("response");
            
            // 실제 네이버 사용자 정보
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("user_id", naverResponse.get("id"));
            userInfo.put("name", naverResponse.get("name"));              // 실제 이름
            userInfo.put("email", naverResponse.get("email"));            // 실제 이메일
            userInfo.put("profileImage", naverResponse.get("profile_image"));
            userInfo.put("nickname", naverResponse.get("nickname"));
            userInfo.put("provider", "naver");
            
            System.out.println("네이버 사용자 정보: " + userInfo);
            return userInfo;
            
        } catch (Exception e) {
            throw new RuntimeException("사용자 정보 요청 실패: " + e.getMessage(), e);
        }
    }
    
 // 카카오 Access Token 받기
    private String getKakaoAccessToken(String code) {
        try {
            String clientId = "d3274d532da930a23835ed9d1443e15b";
            String redirectUri = "http://localhost:3000/kakao/callback"; // 정확한 URI
            
            System.out.println("=== 카카오 토큰 요청 ===");
            System.out.println("Client ID: " + clientId);
            System.out.println("Redirect URI: " + redirectUri);
            System.out.println("Code: " + code);
            
            String url = "https://kauth.kakao.com/oauth/token";
            
            // POST 요청 파라미터 준비
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            String params = "grant_type=authorization_code"
                    + "&client_id=" + clientId
                    + "&redirect_uri=" + redirectUri
                    + "&code=" + code;
            
            HttpEntity<String> request = new HttpEntity<>(params, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            Map<String, Object> responseBody = response.getBody();
            String accessToken = (String) responseBody.get("access_token");
            
            System.out.println("카카오 Access Token 받음: " + accessToken);
            return accessToken;
            
        } catch (Exception e) {
            throw new RuntimeException("카카오 Access Token 요청 실패: " + e.getMessage(), e);
        }
    }

    // 카카오 사용자 정보 받기
    private Map<String, Object> getKakaoUserInfo(String accessToken) {
        try {
            String url = "https://kapi.kakao.com/v2/user/me";
            
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            
            HttpEntity<?> request = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            
            Map<String, Object> responseBody = response.getBody();
            Map<String, Object> kakaoAccount = (Map<String, Object>) responseBody.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            
            // 실제 카카오 사용자 정보
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("user_id", responseBody.get("id").toString()); // 카카오 ID (숫자를 문자열로)
            userInfo.put("name", profile.get("nickname"));              // 실제 닉네임
            userInfo.put("email", kakaoAccount.get("email"));           // 실제 이메일
            userInfo.put("profileImage", profile.get("profile_image_url"));
            userInfo.put("nickname", profile.get("nickname"));
            userInfo.put("provider", "kakao");
            
            System.out.println("카카오 사용자 정보: " + userInfo);
            return userInfo;
            
        } catch (Exception e) {
            throw new RuntimeException("카카오 사용자 정보 요청 실패: " + e.getMessage(), e);
        }
    }
}
