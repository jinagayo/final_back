package com.spark.controller;

import com.spark.dto.BoardDTO;
import com.spark.service.BoardService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/board")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class BoardController {
    
    @Autowired
    private BoardService boardService;
    
    
    @GetMapping("/list")
    public ResponseEntity<ApiResponse> getBoardList(
            @RequestParam("boardnum") String boardnum,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", defaultValue = "") String search,
            @RequestParam(value = "sortBy", defaultValue = "latest") String sortBy,
            @RequestParam(value = "filterBy", defaultValue = "all") String filterBy) {
        
        try {
            // 페이지네이션을 위한 Pageable 객체 생성 (0-based indexing)
            Pageable pageable = PageRequest.of(page - 1, size, getSort(sortBy));
            
            // 검색 조건에 따른 게시글 조회
            Page<BoardDTO> boardPage = boardService.getBoardList(boardnum, search, pageable);
    
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
    public ResponseEntity<ApiResponse> createBoard(@RequestBody BoardDTO boardDTO, @PathVariable String boardnum,HttpServletRequest request) {
        try {            
            HttpSession session = request.getSession();
            String userId = (String)session.getAttribute("login");
            
            if(userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "로그인이 필요합니다.", null, 0));
            }
            boardDTO.setUser_id(userId);
            boardDTO.setBoardnum(boardnum);
            
            BoardDTO createdBoard = boardService.createBoard(boardDTO);
            
            return ResponseEntity.ok(new ApiResponse(true, "게시글 작성 성공", createdBoard, 0));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "게시글 작성 실패: " + e.getMessage(), null, 0));
        }
    }
    
    //게시글 수정
    @PutMapping("/{boardId}")
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
}