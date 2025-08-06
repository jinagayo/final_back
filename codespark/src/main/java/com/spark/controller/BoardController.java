package com.spark.controller;

import com.spark.controller.ClassController.ApiResponse;
import com.spark.dto.ApiResponseComment;
import com.spark.dto.BoardDTO;
import com.spark.dto.CommentDTO;
import com.spark.dto.CommentRequestDTO;
import com.spark.service.BoardService;
import com.spark.service.CodingService;
import com.spark.service.CommentService;
import com.spark.service.S3Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/board")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class BoardController {

    private final CodingService codingService;
    
    @Autowired
    private BoardService boardService;
    
    @Autowired
    private CommentService commService;
    
    @Autowired
    private S3Service s3Service;


    BoardController(CodingService codingService) {
        this.codingService = codingService;
    }
    
    
    @GetMapping("/list")
    public ResponseEntity<ApiResponse> getBoardList(
		    @RequestParam("boardnum") String boardnum,
		    @RequestParam(value = "classId", required = false) String classId,
		    @RequestParam(value = "page", defaultValue = "1") int page,
		    @RequestParam(value = "size", defaultValue = "10") int size,
		    @RequestParam(value = "search", defaultValue = "") String search,
		    @RequestParam(value = "sortBy", defaultValue = "latest") String sortBy,
		    @RequestParam(value = "filterBy", defaultValue = "all") String filterBy) {

		    try {
		
			    // 페이지네이션을 위한 Pageable 객체 생성 (0-based indexing)
			    Pageable pageable = PageRequest.of(page - 1, size, getSort(sortBy));
			    Page<BoardDTO> boardPage;
			    if(classId != null && !classId.isEmpty()) {
			    	boardPage = boardService.getBoardList(classId,boardnum, search, pageable);
			    }else {
			    	boardPage = boardService.getBoardList("",boardnum, search, pageable);
			    }

	    // 검색 조건에 따른 게시글 조회
	
	    // React 컴포넌트에서 기대하는 형식으로 데이터 변환
	    List<Map<String, Object>> notices = boardPage.getContent().stream()
	    .map(this::convertToNoticeFormat)
	    .toList();
	
	    // 응답 데이터 구성
	    Map<String, Object> responseData = new HashMap<>();
	    responseData.put("notices", notices);
	    responseData.put("currentPage", page);
	    responseData.put("totalPages", boardPage.getTotalPages());
	    responseData.put("totalElements", boardPage.getTotalElements());
	    responseData.put("hasNext", boardPage.hasNext());
	    responseData.put("hasPrevious", boardPage.hasPrevious());
	
	    return ResponseEntity.ok(new ApiResponse(true, "게시글 조회 성공", notices, boardPage.getTotalPages()));

	    } catch (Exception e) {
		    e.printStackTrace();
		    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		    .body(new ApiResponse(false, "게시글 조회 실패: " + e.getMessage(), null, 0));
    	}
    }
    
    //조회수
    @PostMapping("/notices/{boardId}/view")
    public ResponseEntity<ApiResponse> increaseViewCount(@PathVariable int boardId) {
        try {
            boardService.increaseHits(boardId);
            return ResponseEntity.ok(new ApiResponse(true, "조회수 증가 성공", null, 0));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "조회수 증가 실패: " + e.getMessage(), null, 0));
        }
    }
    
    //게시물 상세
    @GetMapping("/detail/{boardId}")
    public ResponseEntity<ApiResponse> getBoardDetail(@PathVariable int boardId) {
        try {
            BoardDTO board = boardService.getBoardById(boardId);
            if (board == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false, "게시글을 찾을 수 없습니다.", null, 0));
            }
            
            Map<String, Object> notice = convertToNoticeFormat(board);
            return ResponseEntity.ok(new ApiResponse(true, "게시글 조회 성공", notice, 0));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "게시글 조회 실패: " + e.getMessage(), null, 0));
        }
    }
    
  //게시글 작성
    @PostMapping("/write/{boardnum}")
    public ResponseEntity<ApiResponse> createBoard(
    		  @RequestParam("title") String title,
    	      @RequestParam("content") String content,
    	      @RequestParam("boardnum") String boardnum,
    	      @RequestParam(value = "file", required = false) MultipartFile file,
    	      HttpSession session
    	      )throws IOException {
        try {            
            String userId = (String)session.getAttribute("login");
            
            if(userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "로그인이 필요합니다.", null, 0));
            }
            //S3업로드
            String s3Key = s3Service.upload(file, "board");
            String assignmentUrl = "https://my-lecture-video.s3.ap-northeast-2.amazonaws.com/" + s3Key;
            
            BoardDTO board = new BoardDTO();
            board.setTitle(title);
            board.setContent(content);
            board.setBoardnum(boardnum);
            board.setFile(assignmentUrl);
            board.setUser_id(userId);
            
            BoardDTO createdBoard = boardService.createBoard(board);
            return ResponseEntity.ok(new ApiResponse(true, "게시글 작성 성공", board, 0));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "게시글 작성 실패: " + e.getMessage(), null, 0));
        }
    }
    
    //게시글 수정
    @PutMapping("/edit/{boardId}")
    public ResponseEntity<ApiResponse> updateBoard(@PathVariable int boardId, @RequestBody BoardDTO boardDTO) {
        try {
            boardDTO.setBoard_id(boardId);
            BoardDTO updatedBoard = boardService.updateBoard(boardDTO);
            return ResponseEntity.ok(new ApiResponse(true, "게시글 수정 성공", updatedBoard, 0));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "게시글 수정 실패: " + e.getMessage(), null, 0));
        }
    }
    
    @DeleteMapping("/{boardId}")
    public ResponseEntity<ApiResponse> deleteBoard(@PathVariable int boardId) {
        try {
            boardService.deleteBoard(boardId);
            return ResponseEntity.ok(new ApiResponse(true, "게시글 삭제 성공", null, 0));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "게시글 삭제 실패: " + e.getMessage(), null, 0));
        }
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
        notice.put("category", getCategoryFromBoardnum(board.getBoardnum()));
        notice.put("isPinned", false); // 기본값, 필요시 로직 추가
        notice.put("pinned", false);   // 기본값, 필요시 로직 추가
        notice.put("createdAt", java.time.LocalDateTime.now().toString()); // 실제로는 DB에서 가져와야 함
        notice.put("boardnum", board.getBoardnum());
        notice.put("classId", board.getClass_id());
        notice.put("file", board.getFile());
        return notice;
    }
    
    private String getCategoryFromBoardnum(String boardnum) {
        if (boardnum == null) return "일반";
        
        switch (boardnum) {
            case "1": return "공지";
            case "2": return "자유";
            case "3": return "Q&A";
            default: return "일반";
        }
    }
    
    private Sort getSort(String sortBy) {
        switch (sortBy) {
            case "oldest":
                return Sort.by(Sort.Direction.ASC, "boardId"); // 생성일이 없으므로 ID로 대체
            case "views":
                return Sort.by(Sort.Direction.DESC, "hits");
            case "latest":
            default:
                return Sort.by(Sort.Direction.DESC, "boardId"); // 생성일이 없으므로 ID로 대체
        }
    }
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
    
    //댓글조회
    @GetMapping("/{boardno}/comments")
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
    @PostMapping("/{boardno}/comments")
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
    @PutMapping("/comments/{commentId}")
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
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponseComment<Void>> deleteComment(
            @PathVariable("commentId") int commentId,
            HttpServletRequest httpRequest) {
        
        try {
            String userId = getCurrentUserId(httpRequest);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseComment.error("로그인이 필요합니다."));
            }
            
            commService.deleteComment(commentId, userId);
            return ResponseEntity.ok(ApiResponseComment.success("댓글 삭제 성공", null));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseComment.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponseComment.error("댓글 삭제 중 오류가 발생했습니다."));
        }
    }
    
}