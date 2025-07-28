package com.spark.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.shaded.gson.Gson;
import com.spark.Entity.CodingEntity;
import com.spark.dto.CodingDTO;
import com.spark.repository.CodingRepository;
import com.spark.repository.TestCaseRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class CodingService {

    private final CourseService courseService;
	@Autowired
	private CodingRepository codingRepo;
	
	@Autowired
	private TestCaseRepository testCaseRepo;

    CodingService(CourseService courseService) {
        this.courseService = courseService;
    }
	
	//권한검증 및 입력값 검증
	public Map<String, Object> createProblemWithValidation(CodingDTO codingDTO, HttpSession session){
		Map<String , Object> response = new HashMap<>();
		
		System.out.println("=== createProblemWithValidation 시작 ===");
		
		//권한검증
		Map<String, Object> authResult = validateUserPermission(session);
		Boolean authSuccess = (Boolean) authResult.get("success");
		if(authSuccess == null || !authSuccess) {
			System.out.println("권한검증 실패: " + authResult);
			return authResult;
		}
		
		String userId = (String)authResult.get("userId");
		System.out.println("권한검증 통과, userId: " + userId);
		
		//입력값 검증
		Map<String, Object> validationResult = validateProblemInput(codingDTO);
		Boolean validationSuccess = (Boolean) validationResult.get("success");
		if(validationSuccess == null || !validationSuccess) {
			System.out.println("입력값검증 실패: " + validationResult);
			return validationResult;
		}
		
		System.out.println("입력값검증 통과");
		
		//문제 저장
		try {
			int codingId = createProblem(codingDTO, userId);
			
			response.put("success", true);
			response.put("message", "문제가 등록되었습니다.");
			response.put("codingId", codingId);
			
			System.out.println("문제 저장 성공, ID: " + codingId);
			return response;
		}catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("message", "문제 저장 중 오류가 발생했습니다: " + e.getMessage());
			response.put("statusCode", 500);
			System.out.println("문제 저장 실패: " + e.getMessage());
			return response;
		}
	}
	
	//사용자 권한 검증
    private Map<String, Object> validateUserPermission(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        String userId = (String) session.getAttribute("login");
        String position = (String) session.getAttribute("position");
        
        // 디버깅 로그 추가
        System.out.println("=== 권한 검증 디버깅 ===");
        System.out.println("Session ID: " + session.getId());
        System.out.println("userId: " + userId);
        System.out.println("position: " + position);
        System.out.println("position type: " + (position != null ? position.getClass().getSimpleName() : "null"));
        
        if (userId == null) {
            System.out.println("userId가 null - 401 반환");
            result.put("success", false);
            result.put("message", "로그인이 필요합니다.");
            result.put("statusCode", 401);
            return result;
        }
        
        if (!"2".equals(position) && !"3".equals(position)) {
            System.out.println("권한 없음 - position: [" + position + "]");
            System.out.println("\"2\".equals(position): " + "2".equals(position));
            System.out.println("\"3\".equals(position): " + "3".equals(position));
            result.put("success", false);
            result.put("message", "문제 등록 권한이 없습니다. (강사 또는 관리자만 가능)");
            result.put("statusCode", 403);
            return result;
        }
        
        System.out.println("권한 검증 통과");
        result.put("success", true);
        result.put("userId", userId);
        return result;
    }
	
    
    //입력값 검증
    private Map<String, Object> validateProblemInput(CodingDTO codingDTO) {
        Map<String, Object> result = new HashMap<>();
        
        System.out.println("=== 입력값 검증 시작 ===");
        System.out.println("받은 데이터: " + codingDTO);
        
        //제목 검증
        if(codingDTO.getTitle() == null || codingDTO.getTitle().trim().isEmpty()) {
        	System.out.println("제목이 비어있음");
        	result.put("success", false);
        	result.put("message", "제목을 입력해주세요");
        	result.put("statusCode", 400);
        	return result;
        }
        
        // 문제 검증
        if(codingDTO.getQeustion() == null || codingDTO.getQeustion().trim().isEmpty()) {
            System.out.println("문제 내용이 비어있음");
            result.put("success", false);
            result.put("message", "문제 내용을 입력해주세요.");
            result.put("statusCode", 400);
            return result;
        }
        
        // 모범답안 검증
        if(codingDTO.getModel_answer() == null || codingDTO.getModel_answer().trim().isEmpty()) {
            System.out.println("모범답안이 비어있음");
            result.put("success", false);
            result.put("message", "모범답안을 입력해주세요");
            result.put("statusCode", 400);
            return result;
        }
        
        // 난이도 검증
        if(codingDTO.getLevel() < 1 || codingDTO.getLevel() > 3) {
            System.out.println("난이도가 범위를 벗어남: " + codingDTO.getLevel());
            result.put("success", false);
            result.put("message", "난이도를 선택해주세요");
            result.put("statusCode", 400);
            return result;
        }
        
        // 언어 검증
        String[] allowedLanguages = {"java","python","javascript","c++","c"};
        if(!Arrays.asList(allowedLanguages).contains(codingDTO.getLanguage())) {
            System.out.println("지원하지 않는 언어: " + codingDTO.getLanguage());
            result.put("success", false);
            result.put("message", "지원하지 않는 프로그래밍 언어입니다.");
            result.put("statusCode", 400);
            return result;
        }
        
        System.out.println("입력값 검증 통과");        
        result.put("success", true);
        return result;
    }
	
    
    //데이터 저장 로직
  //데이터 저장 로직
    public int createProblem(CodingDTO codingDTO, String createBy) {
        try {
            System.out.println("=== 데이터 저장 시작 ===");
            
            CodingEntity coding = new CodingEntity();
            coding.setTitle(codingDTO.getTitle().trim());
            coding.setQeustion(codingDTO.getQeustion().trim());
            coding.setLevel(codingDTO.getLevel());
            coding.setLanguage(codingDTO.getLanguage().trim());
            coding.setFiled(codingDTO.getFiled().trim());
            coding.setModelAnswer(codingDTO.getModel_answer().trim());
            coding.setCreateAt(LocalDateTime.now());
            coding.setCreateBy(createBy);
            coding.setUpdateAt(LocalDateTime.now());
            
            // 테스트케이스가 이미 String이라면 그대로 저장
            if (codingDTO.getTest_case() != null && !codingDTO.getTest_case().trim().isEmpty()) {
                System.out.println("테스트케이스 데이터: " + codingDTO.getTest_case());
                coding.setTestCase(codingDTO.getTest_case()); // 그대로 저장
            }

            System.out.println("엔티티 저장 전 데이터 확인");
            System.out.println("Question: " + coding.getQeustion());
            System.out.println("Level: " + coding.getLevel());
            System.out.println("Language: " + coding.getLanguage());
            
            CodingEntity saveCoding = codingRepo.save(coding);
            System.out.println("저장 완료, ID: " + saveCoding.getCodeId());
            
            return saveCoding.getCodeId();
            
        } catch (Exception e) {
            System.out.println("저장 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("저장 실패: " + e.getMessage());
        }
    }

    // convertTestCasesToJson 메서드
    private String convertTestCasesToJson(List<?> testCases) {
        try {
            System.out.println("테스트케이스 변환 시작: " + testCases);
            String json = new Gson().toJson(testCases);
            System.out.println("변환된 JSON: " + json);
            return json;
        } catch (Exception e) {
            System.out.println("JSON 변환 실패: " + e.getMessage());
            return "[]";
        }
    }
}