package com.spark.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spark.Entity.CodingEntity;
import com.spark.dto.CodingDTO;
import com.spark.repository.CodingRepository;
import com.spark.repository.TestCaseRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class CodingService {
	@Autowired
	private CodingRepository codingRepo;
	
	@Autowired
	private TestCaseRepository testCaseRepo;
	
	//권한검증 및 입력값 검증
	public Map<String, Object> createProblemWithValidation(CodingDTO codingDTO, HttpSession session){
		Map<String , Object> response = new HashMap<>();
		
		//권한검증
		Map<String, Object> authResult = validateUserPermission(session);
		if(!(Boolean) authResult.get("success")) {
			return authResult;
		}
		
		String userId = (String)authResult.get("userId");
		
		//입력값 검증
		Map<String, Object> validationResult = validateProblemInput(codingDTO);
		if(!(Boolean) validationResult.get("success")) {
			return validationResult;
		}
		
		//문제 저장
		try {
			int coidngId = createProblem(codingDTO,userId);
			
			response.put("success", true);
			response.put("message", "문제가 등록되었습니다.");
			response.put("codingId", coidngId);
			
			return response;
		}catch (Exception e) {
			response.put("success", false);
			response.put("message", "문제 저장 중 오류가 발생했습니다." + e.getMessage());
			return response;
		}
	}
	
	//사용자 권한 검증
    private Map<String, Object> validateUserPermission(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        String userId = (String) session.getAttribute("login");
        String position = (String) session.getAttribute("position");
        
        if (userId == null) {
            result.put("success", false);
            result.put("message", "로그인이 필요합니다.");
            result.put("statusCode", 401);
            return result;
        }
        
        if (!"2".equals(position) && !"3".equals(position)) {
            result.put("success", false);
            result.put("message", "문제 등록 권한이 없습니다. (강사 또는 관리자만 가능)");
            result.put("statusCode", 403);
            return result;
        }
        
        result.put("success", true);
        result.put("userId", userId);
        return result;
    }
	
    
    //입력값 검증
    private Map<String, Object> validateProblemInput(CodingDTO codingDTO) {
    	Map<String, Object> result = new HashMap<>();
    	
    	//문제 검증
    	if(codingDTO.getQeustion() == null || codingDTO.getQeustion().trim().isEmpty()) {
    		result.put("sucess",false);
    		result.put("message", "문제 내용을 입력해주세요.");
    		return result;
    	}
    	
    	//모범답안 검증
    	if(codingDTO.getModel_answer() == null || codingDTO.getModel_answer().trim().isEmpty()) {
    		result.put("success", false);
    		result.put("message", "모범답안을 입력해주세요");
    		return result;
    	}
    	
    	//난이도 검증
    	if(codingDTO.getLevel() < 1 || codingDTO.getLevel() > 3) {
    		result.put("success", false);
    		result.put("message", "난이도롤 선택해주세요");
    		return result;
    	}
    	
    	//언어 검증
    	String[] allowedLanguages = {"java","python","javascript","c++","c"};
    	if(!Arrays.asList(allowedLanguages).contains(codingDTO.getLanguage())) {
    		result.put("success", false);
    		result.put("message", "지원하지 않는 프로그래밍 언어입니다.");
    		return result;
    	}
    	
    	//테스트 케이스 검증
    	if(codingDTO.getTest_case() == null || codingDTO.getTest_case().isEmpty()) {
    		result.put("success", false);
    		result.put("message", "최소 하나의 테스트케이스를 입력해주세요");
    		return result;
    	}
    	
    	result.put("success",true);
    	return result;
    }
	
    
    //데이터 저장 로직
    public int createProblem(CodingDTO codingDTO,String createBy) {
    	try {
    		CodingEntity coding = new CodingEntity();
    		coding.setQeustion(codingDTO.getQeustion().trim());
    		coding.setLevel(codingDTO.getLevel());
    		coding.setLanguage(codingDTO.getLanguage().trim());
    		coding.setFiled(codingDTO.getFiled().trim());
    		coding.setModelAnswer(codingDTO.getModel_answer().trim());
    		coding.setCreateAt(LocalDateTime.now());
    		coding.setCreateBy(createBy);
    		coding.setUpdateAt(LocalDateTime.now());
    	}catch (Exception e) {
			e.printStackTrace();
		}
    	return 0;
    }
}

