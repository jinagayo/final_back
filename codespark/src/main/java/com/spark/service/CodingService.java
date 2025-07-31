package com.spark.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort; // 올바른 import
import org.springframework.http.ResponseEntity;
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

    // ========== 기존 문제 생성 관련 메서드들 (그대로 유지) ==========
    
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
    
    private Map<String, Object> validateUserPermission(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        String userId = (String) session.getAttribute("login");
        String position = (String) session.getAttribute("position");
        
        System.out.println("=== 권한 검증 디버깅 ===");
        System.out.println("Session ID: " + session.getId());
        System.out.println("userId: " + userId);
        System.out.println("position: " + position);
        
        if (userId == null) {
            System.out.println("userId가 null - 401 반환");
            result.put("success", false);
            result.put("message", "로그인이 필요합니다.");
            result.put("statusCode", 401);
            return result;
        }
        
        if (!"2".equals(position) && !"3".equals(position)) {
            System.out.println("권한 없음 - position: [" + position + "]");
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
    
    private Map<String, Object> validateProblemInput(CodingDTO codingDTO) {
        Map<String, Object> result = new HashMap<>();
        
        System.out.println("=== 입력값 검증 시작 ===");
        System.out.println("받은 데이터: " + codingDTO);
        
        if(codingDTO.getTitle() == null || codingDTO.getTitle().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "제목을 입력해주세요");
            result.put("statusCode", 400);
            return result;
        }
        
        if(codingDTO.getQeustion() == null || codingDTO.getQeustion().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "문제 내용을 입력해주세요.");
            result.put("statusCode", 400);
            return result;
        }
        
        if(codingDTO.getModel_answer() == null || codingDTO.getModel_answer().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "모범답안을 입력해주세요");
            result.put("statusCode", 400);
            return result;
        }
        
        if(codingDTO.getLevel() < 1 || codingDTO.getLevel() > 3) {
            result.put("success", false);
            result.put("message", "난이도를 선택해주세요");
            result.put("statusCode", 400);
            return result;
        }
        
        String[] allowedLanguages = {"java","python","javascript","c++","c"};
        if(!Arrays.asList(allowedLanguages).contains(codingDTO.getLanguage())) {
            result.put("success", false);
            result.put("message", "지원하지 않는 프로그래밍 언어입니다.");
            result.put("statusCode", 400);
            return result;
        }
        
        System.out.println("입력값 검증 통과");        
        result.put("success", true);
        return result;
    }
    
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
            coding.setIsActive(1); // 활성 상태로 설정
            
            if (codingDTO.getTest_case() != null && !codingDTO.getTest_case().trim().isEmpty()) {
                System.out.println("테스트케이스 데이터: " + codingDTO.getTest_case());
                coding.setTestCase(codingDTO.getTest_case());
            }

            CodingEntity saveCoding = codingRepo.save(coding);
            System.out.println("저장 완료, ID: " + saveCoding.getCodeId());
            
            return saveCoding.getCodeId();
            
        } catch (Exception e) {
            System.out.println("저장 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("저장 실패: " + e.getMessage());
        }
    }

    // ========== 수정된 문제 조회 메서드 ==========
    
    /**
     * 코딩 문제 조회 (수정된 버전)
     */
    public ResponseEntity<Map<String, Object>> getCodingListWithPaging(
            int page, int size, String search, String sortBy, String level) {
        
        try {
            System.out.println("=== getCodingListWithPaging 시작 ===");
            System.out.println("파라미터 - page: " + page + ", size: " + size + ", search: '" + search + "', sortBy: " + sortBy + ", level: " + level);
            
            // 1. 파라미터 검증 및 정제
            String cleanedSearch = cleanSearchTerm(search);
            Integer validatedLevel = processLevelFilter(level);
            Sort sort = createSort(sortBy); // 정렬 기능 활성화
            
            System.out.println("정제된 파라미터 - search: '" + cleanedSearch + "', level: " + validatedLevel + ", sort: " + sort);
            
            // 2. 페이징 객체 생성
            Pageable pageable = PageRequest.of(
                validatePage(page) - 1, // JPA는 0부터 시작
                validatePageSize(size),
                sort // 정렬 적용
            );
            
            System.out.println("페이징 객체: " + pageable);
            
            // 3. Repository를 통한 데이터 조회
            Page<CodingEntity> entityPage = codingRepo.findByAllConditions(cleanedSearch, validatedLevel, pageable);
            
            System.out.println("조회된 데이터 - 총 개수: " + entityPage.getTotalElements() + ", 현재 페이지 개수: " + entityPage.getNumberOfElements());
            
            // 4. Entity를 DTO로 변환
            List<CodingDTO> problems = entityPage.getContent().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            System.out.println("변환된 DTO 개수: " + problems.size());
            
            // 5. 응답 데이터 구성
            Map<String, Object> response = buildSuccessResponse(problems, entityPage, page, size);
            
            System.out.println("응답 데이터 구성 완료");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("에러 발생: " + e.getMessage());
            e.printStackTrace();
            
            // 6. 에러 응답 처리
            Map<String, Object> errorResponse = buildErrorResponse(e);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * JPA Sort 객체 생성 (활성화)
     */
    private Sort createSort(String sortBy) {
        switch (validateSortBy(sortBy)) {
            case "latest":
                return Sort.by(Sort.Direction.DESC, "createAt");
            case "oldest":
                return Sort.by(Sort.Direction.ASC, "createAt");
            case "level":
                return Sort.by(Sort.Direction.ASC, "level");
            case "correctRate":
                // 실제로는 서브쿼리나 조인이 필요하지만, 기본 정렬 사용
                return Sort.by(Sort.Direction.DESC, "createAt");
            default:
                return Sort.by(Sort.Direction.DESC, "createAt");
        }
    }
    
    /**
     * Entity를 DTO로 변환 (수정된 버전)
     */
    private CodingDTO convertToDTO(CodingEntity entity) {
        CodingDTO dto = new CodingDTO();
        
        // 기본 필드 매핑
        dto.setCode_id(entity.getCodeId());
        dto.setTitle(entity.getTitle());
        dto.setQeustion(entity.getQeustion());
        dto.setType(entity.getType());
        dto.setLevel(entity.getLevel());
        dto.setFiled(entity.getFiled());
        dto.setLanguage(entity.getLanguage());
        dto.setTest_case(entity.getTestCase());
        dto.setModel_answer(entity.getModelAnswer());
        dto.setCreate_at(entity.getCreateAt());
        dto.setCreate_by(entity.getCreateBy());
        dto.setUpdate_at(entity.getUpdateAt());
        dto.setUpdate_by(entity.getUpdateBy());
        dto.setIs_active(entity.getIsActive());
        
        // 프론트엔드에서 필요한 추가 필드들 설정
        dto.setAuthor(entity.getCreateBy());
        dto.setCorrectRate(calculateCorrectRate(entity.getCodeId()));
        dto.setSubmissions(getSubmissionCount(entity.getCodeId()));
        dto.setSolved(checkIfSolved(entity.getCodeId()));
        
        return dto;
    }
    
    /**
     * 검색어 정제
     */
    private String cleanSearchTerm(String search) {
        if (search == null || search.trim().isEmpty()) {
            return null;
        }
        String cleaned = search.trim();
        cleaned = cleaned.replaceAll("[';\"\\\\]", "");
        return cleaned.isEmpty() ? null : cleaned;
    }
    
    /**
     * 레벨 필터 처리
     */
    private Integer processLevelFilter(String level) {
        if (level == null || "all".equals(level.trim().toLowerCase())) {
            return null;
        }
        
        try {
            int levelInt = Integer.parseInt(level.trim());
            if (levelInt >= 1 && levelInt <= 5) {
                return levelInt;
            }
        } catch (NumberFormatException e) {
            // 잘못된 레벨 값은 null로 처리
        }
        
        return null;
    }
    
    /**
     * 정렬 조건 검증
     */
    private String validateSortBy(String sortBy) {
        if (sortBy == null) {
            return "latest";
        }
        
        String[] validSortOptions = {"latest", "oldest", "level", "correctRate"};
        for (String option : validSortOptions) {
            if (option.equals(sortBy.trim())) {
                return sortBy.trim();
            }
        }
        
        return "latest"; // 기본값
    }
    
    /**
     * 페이지 번호 검증
     */
    private int validatePage(int page) {
        return page <= 0 ? 1 : page;
    }
    
    /**
     * 페이지 크기 검증
     */
    private int validatePageSize(int size) {
        if (size <= 0) {
            return 10;
        }
        if (size > 100) { // 최대 100개로 제한
            return 100;
        }
        return size;
    }
    
    /**
     * 성공 응답 데이터 구성
     */
    private Map<String, Object> buildSuccessResponse(
            List<CodingDTO> problems, Page<CodingEntity> page, int currentPage, int size) {
        
        Map<String, Object> response = new HashMap<>();
        
        // 기본 데이터
        response.put("content", problems);
        response.put("totalPages", page.getTotalPages());
        response.put("totalElements", page.getTotalElements());
        response.put("currentPage", currentPage);
        response.put("size", size);
        
        // 페이징 정보
        response.put("first", page.isFirst());
        response.put("last", page.isLast());
        response.put("empty", page.isEmpty());
        response.put("numberOfElements", page.getNumberOfElements());
        
        // 메타 정보
        response.put("success", true);
        response.put("timestamp", System.currentTimeMillis());
        
        System.out.println("빌드된 응답: " + response);
        
        return response;
    }
    
    /**
     * 에러 응답 데이터 구성
     */
    private Map<String, Object> buildErrorResponse(Exception e) {
        Map<String, Object> errorResponse = new HashMap<>();
        
        errorResponse.put("success", false);
        errorResponse.put("error", "데이터 조회 중 오류가 발생했습니다.");
        errorResponse.put("message", e.getMessage());
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        // 개발환경에서만 상세 정보 제공
        if (isDevelopmentMode()) {
            errorResponse.put("detailMessage", e.toString());
        }
        
        return errorResponse;
    }
    
    /**
     * 개발 모드 확인
     */
    private boolean isDevelopmentMode() {
        String profile = System.getProperty("spring.profiles.active");
        return "dev".equals(profile) || "development".equals(profile);
    }
    
    /**
     * 정답률 계산 (임시)
     */
    private double calculateCorrectRate(int codeId) {
        return Math.round((Math.random() * 80 + 20) * 10.0) / 10.0;
    }
    
    /**
     * 제출 수 계산 (임시)
     */
    private int getSubmissionCount(int codeId) {
        return (int)(Math.random() * 200) + 10;
    }
    
    /**
     * 해결 여부 확인 (임시)
     */
    private boolean checkIfSolved(int codeId) {
        return Math.random() > 0.5;
    }
    
    /**
     * 난이도 태그 생성
     */
    private String getDifficultyTag(int level) {
        switch (level) {
            case 1: return "초급";
            case 2: return "중급";
            case 3: return "고급";
            default: return "미정";
        }
    }
    
    /**
     * 난이도별 문제 개수 조회
     */
    public Map<Integer, Long> getLevelStats() {
        List<Object[]> results = codingRepo.countByLevel();
        Map<Integer, Long> stats = new HashMap<>();
        
        for (Object[] result : results) {
            Integer level = (Integer) result[0];
            Long count = (Long) result[1];
            stats.put(level, count);
        }
        
        return stats;
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
    
    //코딩문제 상세
    public CodingDTO getCodingDetail(int codeId) {
        try {
            System.out.println("=== 문제 상세 조회 서비스 시작 ===");
            System.out.println("조회할 문제 ID: " + codeId);
            
            // Repository에서 문제 조회
            Optional<CodingEntity> optionalEntity = codingRepo.findByCodeIdAndIsActive(codeId, 1);
            
            if (!optionalEntity.isPresent()) {
                System.out.println("문제를 찾을 수 없음: ID = " + codeId);
                return null;
            }
            
            CodingEntity entity = optionalEntity.get();
            System.out.println("조회된 문제: " + entity.getTitle());
            
            // Entity를 DTO로 변환
            CodingDTO dto = convertToDetailDTO(entity);
            
            System.out.println("DTO 변환 완료");
            return dto;
            
        } catch (Exception e) {
            System.err.println("문제 상세 조회 서비스 오류: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("문제 상세 조회 실패: " + e.getMessage());
        }
    }
    private CodingDTO convertToDetailDTO(CodingEntity entity) {
        CodingDTO dto = new CodingDTO();
        
        // 기본 필드 매핑
        dto.setCode_id(entity.getCodeId());
        dto.setTitle(entity.getTitle());
        dto.setQeustion(entity.getQeustion());
        dto.setType(entity.getType());
        dto.setLevel(entity.getLevel());
        dto.setFiled(entity.getFiled());
        dto.setLanguage(entity.getLanguage());
        dto.setTest_case(entity.getTestCase());
        dto.setModel_answer(entity.getModelAnswer());
        dto.setCreate_at(entity.getCreateAt());
        dto.setCreate_by(entity.getCreateBy());
        dto.setUpdate_at(entity.getUpdateAt());
        dto.setUpdate_by(entity.getUpdateBy());
        dto.setIs_active(entity.getIsActive());
                
        return dto;
    }
    
}