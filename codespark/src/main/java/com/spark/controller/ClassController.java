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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.spark.service.BoardService;
import com.spark.service.ClassService;
import com.spark.service.CommentService;
import com.spark.service.CourseService;
import com.spark.service.MeterialSubService;
import com.spark.service.S3Service;
import com.spark.Entity.BoardEntity;
import com.spark.Entity.MeterialEntity;
import com.spark.Entity.MeterialSubEntity;
import com.spark.Entity.TestEntity;
import com.spark.Entity.TestSubEntity;
import com.spark.controller.BoardController.ApiResponse;
import com.spark.dto.ApiResponseComment;
import com.spark.dto.BoardDTO;
import com.spark.dto.ClassDTO;
import com.spark.dto.ClassInfoDTO;
import com.spark.dto.CommentDTO;
import com.spark.dto.CommentRequestDTO;
import com.spark.dto.MeterialDTO;
import com.spark.dto.SubjectReviewDTO;
import com.spark.dto.TestDTO;
import com.spark.dto.TestSubDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

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
	
	@Autowired
	private CommentService commService;

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
       
        if (classDto == null) {
            return ResponseEntity.status(404).body("강의 정보를 찾을 수 없습니다.");
        }
        //수업자료
        List<MeterialEntity> meterial = classService.getMeterials(Integer.parseInt(classId));
        

        //수업자료에 따른 수행 여부
        List<Map<String,Object>> meterials= new ArrayList<>();
        for(MeterialEntity m : meterial) {
        	Map<String,Object> map = new HashMap<>();
        	map.put("meterial", m);
        	List<MeterialSubEntity> sub = classService.getMeterialSubOne(String.format("%d", m.getMeterId()), id);
            System.out.println("subsubsubsubsub"+sub);
        	
        	map.put("sub",classService.studentDidIt(sub,m));
        	meterials.add(map);
        }
        
        //리뷰
        Integer attId = classService.getAttId(id,classId);
        Boolean review = classService.reviewYN(attId);
        System.out.println(review);
        
        
        Map<String, Object> map = new HashMap<>();
        map.put("class", classDto);
        map.put("meterials", meterials);
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
    public ResponseEntity<?> test(HttpSession session,@RequestParam("meterial_id") String meterialId) {
    	System.out.println("test 작동중");
        String id = (String)session.getAttribute("login");
    	MeterialEntity meterial = classService.getMeterialOne(meterialId);
    	List<TestEntity> test = classService.getTest(meterialId);
    	Map<String,Object> data = new HashMap<>();
    	data.put("title", meterial.getTitle());
    	data.put("content", meterial.getContent());
    	data.put("time", meterial.getTime());
    	data.put("questions", test);
    	Boolean bool = classService.testYN(meterialId,id);
    	data.put("submit", bool);
    	System.out.println(data);
    	return  ResponseEntity.ok().body(data);
    }

    @PostMapping("testsubmit")
    public ResponseEntity<?> testsubmit(@RequestParam("meterial_id") String meterialId,
    		HttpSession session, @RequestBody List<TestSubDTO> list) {
    	System.out.println("testsubmit 작동중");
        String id = (String)session.getAttribute("login");
        double total =0 , correct=0;
        for(TestSubDTO dto : list) {
        	classService.testSubmit(id,dto);
        	System.out.println("dto"+dto);
        	total++;
        	if(dto.isCorrect()) correct++;
        	
        }
    	classService.materialTestDone(meterialId,id,100*correct/total);
    	
        return ResponseEntity.ok("");
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
    
    @GetMapping("testresult")
    public ResponseEntity<?> testresult(HttpSession session,@RequestParam("meterial_id") String meterialId) {
    	System.out.println("testresult 작동중");
        String id = (String)session.getAttribute("login");
    	Map<String,Object> data = new HashMap<>();

    	//테스트정보
    	MeterialEntity metEntity= classService.getMeterialOne(meterialId);
    	data.put("title", metEntity.getTitle());
    	data.put("content", metEntity.getContent());
    	//시험점수
    	MeterialSubEntity subEntity = classService.getMeterialSubOne(meterialId,id).get(0);
    	data.put("score", subEntity.getContent());
    	//문제정보
    	List<Map<String,Object>> questions = new ArrayList<>();
    	List<TestEntity> testEntity = classService.getTest(meterialId);
    	for(TestEntity t :testEntity) {
    		Map<String,Object> map = new HashMap<>();
    		map.put("question", t.getQuestion());
    		map.put("answer", t.getAnswer());
    		map.put("choice1", t.getChoice1());
    		map.put("choice2", t.getChoice2());
    		map.put("choice3", t.getChoice3());
    		map.put("choice4", t.getChoice4());
    		TestSubEntity sub = classService.getTestSub(String.format("%d", t.getTestId()),id);
    		map.put("submit", sub.getSubmit());
    		map.put("correct", sub.isCorrect());
    		questions.add(map);
    	}
    	data.put("questions", questions);

        return ResponseEntity.ok().body(data);
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
    
    @PostMapping("board/write/{classId}")
    public ResponseEntity<ApiResponse> createBoard(
        @RequestParam("title") String title,
        @RequestParam("content") String content,
        @RequestParam("boardnum") String boardnum,
        @RequestParam("class_id") String class_id,
        @RequestParam(value = "file", required = false) MultipartFile file,
        HttpSession session
    ) throws IOException {
        String userId = (String)session.getAttribute("login");
        
        String assignmentUrl = null;
        
        // 파일이 있을 때만 업로드
        if (file != null && !file.isEmpty()) {
            String s3Key = s3Service.upload(file, "board");
            assignmentUrl = "https://my-lecture-video.s3.ap-northeast-2.amazonaws.com/" + s3Key;
        }

       
        BoardDTO board = new BoardDTO();
        board.setTitle(title);
        board.setContent(content);
        board.setBoardnum(boardnum);
        board.setClass_id(class_id);
        System.out.println("class_id" + class_id);
        board.setFile(assignmentUrl);
        board.setUser_id(userId);
        board.setIs_active(1);
        
        try {
            BoardDTO createdBoard = boardService.createBoardClassId(class_id, board);
            System.out.println("서비스 호출 완료 - 성공");
            return ResponseEntity.ok(new ApiResponse(true, "게시글 작성 성공", createdBoard, 0));
        } catch (Exception e) {
            System.out.println("서비스 호출 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "게시글 작성 실패", null, 0));
        }
    }
    
    //강의별 게시판 상세보기
    @GetMapping("board/detail/{classId}/{boardId}")
    public ResponseEntity<ApiResponse> getBoardDetail(
        @PathVariable String classId,
        @PathVariable int boardId) {
        
        try {
            BoardDTO board = boardService.getBoardById(boardId); // 메서드명 변경!
            
            if (board == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "게시글을 찾을 수 없습니다.", null, 0));
            }
            
            if (!classId.equals(board.getClass_id())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, "해당 강의의 게시글이 아닙니다.", null, 0));
            }
            
            Map<String, Object> notice = convertToNoticeFormat(board);
            return ResponseEntity.ok(new ApiResponse(true, "게시글 조회 성공", notice, 0));
            
        } catch (Exception e) {
            System.out.println("상세보기 오류: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "게시글 조회 실패: " + e.getMessage(), null, 0));
        }
    }
    
    //강의별 게시판 수정
    @GetMapping("board/edit/{classId}/{boardId}")
    public ResponseEntity<ApiResponse> getBoardForEdit(
        @PathVariable int boardId,
        @RequestParam(required = false) String classId, // classId를 파라미터로 받음
        HttpSession session) {

        try {
            System.out.println("=== 게시글 조회 (수정용) ===");
            System.out.println("boardId: " + boardId);
            System.out.println("classId: " + classId);

            String userId = (String) session.getAttribute("login");
            String position = (String) session.getAttribute("position");

            // 로그인 확인
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "로그인이 필요합니다.", null, 0));
            }

            // 게시글 조회 - Entity로 조회해서 권한 체크에 필요한 정보 확인
            BoardEntity boardEntity = boardService.findById(boardId);
            
            if (boardEntity == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "게시글을 찾을 수 없습니다.", null, 0));
            }

            // classId가 제공된 경우 해당 강의의 게시글인지 확인
            if (classId != null && !classId.equals(boardEntity.getClassId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, "해당 강의의 게시글이 아닙니다.", null, 0));
            }

            // 권한 체크 - 본인 글이거나 관리자인지 확인
            boolean isAuthor = userId.equals(boardEntity.getUserId()) || userId.equals(boardEntity.getCreateBy());
            boolean isAdmin = "3".equals(position);

            if (!isAuthor && !isAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, "본인이 작성한 게시글만 수정할 수 있습니다.", null, 0));
            }

            // DTO로 변환하면서 작성자 정보도 포함
            BoardDTO board = boardService.getBoardByclassId(boardId);
            
            // 🔥 작성자 정보 추가 - 프론트엔드 권한 체크를 위해
            if (board != null) {
                board.setUser_id(boardEntity.getUserId());
                board.setCreated_by(boardEntity.getCreateBy());
                board.setUser_id(boardEntity.getUserId());
                board.setCreated_by(boardEntity.getCreateBy());
                board.setIs_active(board.getIs_active());
            }

            return ResponseEntity.ok(new ApiResponse(true, "게시글 조회 성공", board, 0));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "게시글 조회 실패: " + e.getMessage(), null, 0));
        }
    }
    
    @PutMapping("board/edit/{classId}/{boardId}")
    public ResponseEntity<ApiResponse> updateBoard(
        @RequestParam(required = false) String classId, // classId를 파라미터로 받음
        @PathVariable int boardId, 
        @RequestBody BoardDTO boardDTO,
        HttpSession session) {

        try {
            String userId = (String) session.getAttribute("login");
            String position = (String) session.getAttribute("position");

            // 로그인 확인
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "로그인이 필요합니다.", null, 0));
            }

            // 게시글 작성자 확인
            BoardEntity board = boardService.findById(boardId);
            if (board == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "게시글을 찾을 수 없습니다.", null, 0));
            }

            boolean isAuthor = userId.equals(board.getUserId()) || userId.equals(board.getCreateBy());
            boolean isAdmin = "3".equals(position);

            if (!isAuthor && !isAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, "본인이 작성한 게시글만 수정할 수 있습니다.", null, 0));
            }

            boardDTO.setBoard_id(boardId);
            boardDTO.setUser_id(board.getUserId());
            boardDTO.setCreated_by(board.getCreateBy());
            boardDTO.setIs_active(board.getIsActive());

            BoardDTO updatedBoard = boardService.updateBoard(boardDTO);
            return ResponseEntity.ok(new ApiResponse(true, "게시글 수정 성공", updatedBoard, 0));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "게시글 수정 실패: " + e.getMessage(), null, 0));
        }
    }

    //강의별 게시판 삭제
    @DeleteMapping("board/delete/{classId}/{boardId}")
    public ResponseEntity<ApiResponse> deleteBoard(
        @PathVariable String classId,
        @PathVariable int boardId,
        HttpServletRequest request) {
        
        String userId = (String) request.getSession().getAttribute("login");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse(false, "로그인이 필요합니다.", null, 0));
        }
        
        try {
            boardService.deleteBoardByClassId(classId, boardId, userId);
            return ResponseEntity.ok(new ApiResponse(true, "게시글 삭제 성공", null, 0));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "게시글 삭제 실패: " + e.getMessage(), null, 0));
        }
    }
    
    //강의별 게시판 댓글 조회
    @GetMapping("board/{boardno}/comments")
    public ResponseEntity<ApiResponseComment<List<CommentDTO>>> getComments(
            @PathVariable("boardno") int boardno) {
        
        try {
            List<CommentDTO> comments = commService.getCommentsByBoardno(boardno);
            return ResponseEntity.ok(ApiResponseComment.success("댓글 목록 조회 성공", comments));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponseComment.error("댓글 목록 조회 중 오류가 발생했습니다."));
        }
    }
    
    //댓글작성
    @PostMapping("board/{boardno}/comments")
    public ResponseEntity<ApiResponseComment<CommentDTO>> createComment(
            @PathVariable("boardno") int boardno,
            @Valid @RequestBody CommentRequestDTO request,
            HttpServletRequest httpRequest) {
        
        try {
            // 세션에서 사용자 정보 추출 (실제 구현에 맞게 수정);
            String userId = getCurrentUserId(httpRequest);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseComment.error("로그인이 필요합니다."));
            }
            
            CommentDTO comment = commService.createComment(boardno, request, userId);
            return ResponseEntity.ok(ApiResponseComment.success("댓글 작성 성공", comment));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseComment.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponseComment.error("댓글 작성 중 오류가 발생했습니다."));
        }
    }
    
    
    //댓글 수정
    @PutMapping("board/comments/{commentId}")
    public ResponseEntity<ApiResponseComment<CommentDTO>> updateComment(
            @PathVariable("commentId") int commentId,
            @Valid @RequestBody CommentRequestDTO request,
            HttpServletRequest httpRequest) {

        try {
            String userId = getCurrentUserId(httpRequest);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseComment.error("로그인이 필요합니다."));
            }
            CommentDTO comment = commService.updateComment(commentId, request, userId);
            return ResponseEntity.ok(ApiResponseComment.success("댓글 수정 성공", comment));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseComment.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponseComment.error("댓글 수정 중 오류가 발생했습니다."));
        }
    }
    
    private String getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object loginId = session.getAttribute("login");
        if (loginId != null) {
            return loginId.toString();
        }
        return null;
    }
    
    //댓글 삭제
    @DeleteMapping("board/comments/{commentId}")
    public ResponseEntity<ApiResponse> deleteComment(@PathVariable int commentId, HttpSession session) {
        try {
            String userId = (String) session.getAttribute("login");
            String userRole = (String) session.getAttribute("position");
            
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "로그인이 필요합니다.", null, 0));
            }
            
            // ⭐ Service에서 권한 체크까지 처리
            commService.deleteComment(commentId, userId, userRole);
            return ResponseEntity.ok(new ApiResponse(true, "댓글 삭제 성공", null, 0));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse(false, e.getMessage(), null, 0));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "댓글 삭제 실패: " + e.getMessage(), null, 0));
        }
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
    
    private Map<String, Object> convertToNoticeFormat(BoardDTO board) {
        Map<String, Object> notice = new HashMap<>();
        notice.put("id", board.getBoard_id());
        notice.put("title", board.getTitle());
        notice.put("content", board.getContent());
        notice.put("author", board.getUser_id());
        notice.put("createdBy", board.getUser_id());
        notice.put("views", board.getHits());
        notice.put("viewCount", board.getHits());
        notice.put("isPinned", false); // 기본값, 필요시 로직 추가
        notice.put("pinned", false);   // 기본값, 필요시 로직 추가
        notice.put("createdAt", java.time.LocalDateTime.now().toString()); // 실제로는 DB에서 가져와야 함
        notice.put("boardnum", board.getBoardnum());
        notice.put("classId", board.getClass_id());
        notice.put("file", board.getFile());
        return notice;
    }
}