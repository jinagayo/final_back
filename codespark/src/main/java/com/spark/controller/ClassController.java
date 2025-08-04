package com.spark.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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

import com.spark.service.BoardService;
import com.spark.service.ClassService;
import com.spark.service.CourseService;
import com.spark.service.MeterialSubService;
import com.spark.service.S3Service;
import com.spark.Entity.MeterialEntity;
import com.spark.Entity.MeterialSubEntity;
import com.spark.controller.BoardController.ApiResponse;
import com.spark.dto.BoardDTO;
import com.spark.dto.ClassDTO;
import com.spark.dto.ClassInfoDTO;
import com.spark.dto.MeterialDTO;
import com.spark.dto.SubjectReviewDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/myclass/")
@CrossOrigin(origins="http://localhost:3000", allowCredentials="true")
public class ClassController {
    @Autowired
    private ClassService classService;
    @Autowired
    private S3Service s3Service;
    @Autowired
    private MeterialSubService meterialSubService;
    
	@Autowired
	private BoardService boardService;
	
	@Autowired
	private CourseService courseService;
    
    @GetMapping("List")
    public ResponseEntity<?> getAllClass(HttpSession session) {
    	System.out.println("getAllClass 작동중");
        String id = (String)session.getAttribute("login");
    	System.out.println(id);
        List<Map<String, Object>> data = classService.getAllClass(id);
        return ResponseEntity.ok().body(data);
    }    
    
    @GetMapping("Main")
    public ResponseEntity<?> Main(HttpSession session,@RequestParam("class_id") String classId) {
    	System.out.println("Main 작동중");
        String id = (String)session.getAttribute("login");
        //수업 정보
        ClassInfoDTO classDto = classService.getClass(classId);
        //수업자료
        List<MeterialEntity> meterial = classService.getMeterials(Integer.parseInt(classId));
        for(MeterialEntity m : meterial)System.out.println(m);

        //리뷰
        Integer attId = classService.getAttId(id,classId);
        Boolean review = classService.reviewYN(attId);
        System.out.println(review);
        
        Map<String, Object> map = new HashMap<>();
        map.put("class", classDto);
        map.put("material", meterial);
        map.put("review", review);
        
        
    	
        return ResponseEntity.ok().body(map);
    }
    
    @PostMapping("review")
    public ResponseEntity<?> review(HttpSession session,@RequestBody SubjectReviewDTO dto) {
    	System.out.println("review 작동중");
        String id = (String)session.getAttribute("login");
        dto.setAttId(classService.getAttId(id,dto.getClass_id()));
    	System.out.println(dto);
    	classService.saveReview(dto);
    	
    	
        return ResponseEntity.ok().body("");
    }

    @GetMapping("assignment")
    public ResponseEntity<?> assignment(@RequestParam("meterial_id") String meterialId) {
    	System.out.println("assignment 작동중");
    	MeterialEntity data = classService.getMeterialOne(meterialId);
    	System.out.println(data);
    	return  ResponseEntity.ok().body(data);
    }

    @GetMapping("test")
    public ResponseEntity<?> test(@RequestParam("meterial_id") String meterialId) {
    	System.out.println("assignment 작동중");
    	MeterialEntity data = classService.getMeterialOne(meterialId);
    	System.out.println(data);
    	return  ResponseEntity.ok().body(data);
    }

    
    @PostMapping("assignment/submit")
    public ResponseEntity<?> submitAssignment(
    		@RequestParam("file") MultipartFile file,
    		@RequestParam("meterial_id")Integer meterialId,
    		HttpSession session
    ) throws IOException{
    	String studentId = (String)session.getAttribute("login");
    	// S3 업로드 (key는 "assignments/{meterialId}/{학생아이디}/{uuid_파일명}" 이런 식으로)
    	String s3Key = s3Service.upload(file, "assignment");
    	String assignmentUrl =  "https://my-lecture-video.s3.ap-northeast-2.amazonaws.com/" + s3Key;
    	
    	//DB에 s3Key 나 URL 리턴
    	meterialSubService.saveOrUpdateSubmission(meterialId, studentId, s3Key);
    	
    	//프론트에 S3 Key 나 URL 리턴
    	Map<String, Object> result = new HashMap<>();
    	result.put("key", s3Key);
    	result.put("url", assignmentUrl);
    	return ResponseEntity.ok(result);
    	
    }
    
    @GetMapping("student/assignment/{meterialId}/submission")
    public ResponseEntity<?> getStudentSubmission(
    		@PathVariable("meterialId") Integer meterialId,
    		HttpSession session
    ){
    	String studentId = (String)session.getAttribute("login"); // 로그인 세션에서 학생 아이디 가져오기

    	//실제 제출 정보 가져오기
    	MeterialSubEntity submission = meterialSubService.getSubmission(meterialId, studentId);
    	if(submission == null) {
    		return ResponseEntity.status(404).body("제출 내역이 없습니다.");
    	}
    	
    	//프론트에 맞게 JSON 내려주기
    	Map<String, Object> result = new HashMap<>();
    	result.put("content", submission.getContent());
    	result.put("progress", submission.getProgress());
    	
    	return ResponseEntity.ok(result);
    }
    
    //강의별 게시판
    @GetMapping("board/list/{classId}")
    public ResponseEntity<ApiResponse> getClassBoardList(
        @PathVariable("classId") String classId,
        @RequestParam("boardNum") String boardNum,
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(value = "search", defaultValue = "") String search,
        @RequestParam(value = "sortBy", defaultValue = "latest") String sortBy,
        @RequestParam(value = "filterBy", defaultValue = "all") String filterBy) {
        
        try {
            Pageable pageable = PageRequest.of(page - 1, size);
            
            // Service에서 sortBy 파라미터를 전달해서 쿼리에서 처리
            Page<Map<String, Object>> boardPage = boardService.getBoardsByClassId(
                classId, boardNum, search,filterBy, pageable
            );
            
            // 과목 정보도 함께 조회
            Map<String, Object> classInfo = courseService.getSubjectInfo(classId);
            
            // 응답 데이터 구성
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("boards", boardPage.getContent());
            responseData.put("currentPage", page);
            responseData.put("totalPage", boardPage.getTotalPages());
            responseData.put("totalElements", boardPage.getTotalElements());
            responseData.put("size", size);
            responseData.put("subId", classId);
            responseData.put("classtName", classInfo.get("name"));
            responseData.put("boardnum", boardNum);
            
            return ResponseEntity.ok(new ApiResponse(true, "과목별 게시글 조회 성공", responseData, boardPage.getTotalPages()));
            
        } catch (Exception e) {
            System.err.println("과목별 게시글 조회 오류: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            	    .body(new ApiResponse(false, "조회 실패: " + e.getMessage(), null, 0));
        }
    }
    
    //강의별 게시판 글쓰기
    @PostMapping("/board/write/{classId}")
    public ResponseEntity<ApiResponse> createBoard(
        @PathVariable String classId,
        @RequestBody BoardDTO boardDTO,
        HttpServletRequest request
    ) {
        String userId = (String) request.getSession().getAttribute("login");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse(false, "로그인이 필요합니다.", null, 0));
        }

        boardDTO.setUser_id(userId);  // 작성자 ID 설정
        boardDTO.setClass_id(classId);
        BoardDTO createdBoard = boardService.createBoardClassId(classId, boardDTO);

        return ResponseEntity.ok(new ApiResponse(true, "게시글 작성 성공", createdBoard, 0));
    }
    
    //Api
    class ApiResponse {
        private boolean success;
        private String message;
        private Object data;
        private int totalPage;
        
        public ApiResponse(boolean success, String message, Object data, int totalPage) {
            this.success = success;
            this.message = message;
            this.data = data;
            this.totalPage = totalPage;
        }
        
		// getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
        
        public int getTotalPage() { return totalPage; }
        public void setTotalPage(int totalPage) { this.totalPage = totalPage; }
    }
}
