package com.spark.controller;

import com.spark.dto.BoardDTO;
import com.spark.dto.BoardSearchRequest;
import com.spark.service.BoardService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//게시판
@RestController
@RequestMapping("/board")
@CrossOrigin(origins="http://localhost:3000", allowCredentials="true")
public class BoardController {
	
	@Autowired
    private BoardService boardService;

	@GetMapping("/list")
	public ResponseEntity<?> boardList(
	        @RequestParam int boardnum,
	        @RequestParam(defaultValue="1") int page,
	        @RequestParam(defaultValue="10") int size,
	        @RequestParam(defaultValue="") String search,
	        @RequestParam(defaultValue="latest") String sort,
	        @RequestParam(defaultValue="") String category,
	        HttpServletRequest request) {

	    System.out.println("=== 게시판 목록 API 호출 ===");
	    System.out.println("컨트롤러 진입 성공!");
	    System.out.println("요청 URL: " + request.getRequestURL());
	    System.out.println("쿼리 스트링: " + request.getQueryString());
	    System.out.println("요청 메서드: " + request.getMethod());
	    System.out.println("boardnum: " + boardnum);
	    System.out.println("요청 파라미터 - page: " + page + ", size: " + size + 
	                      ", search: '" + search + "', sort: " + sort);

	    try {
	        // boardnum 유효성 검사
	        if (boardnum < 1 || boardnum > 3) {
	            System.out.println("유효하지 않은 게시판 번호: " + boardnum);
	            Map<String, Object> errorResponse = new HashMap<>();
	            errorResponse.put("success", false);
	            errorResponse.put("message", "유효하지 않은 게시판 번호입니다. (1: 공지사항, 2: 자유게시판, 3: QnA)");
	            return ResponseEntity.badRequest().body(errorResponse);
	        }

	        // 요청 객체 생성
	        BoardSearchRequest requestObj = new BoardSearchRequest(String.valueOf(boardnum));
	        requestObj.setPage(page);
	        requestObj.setSize(size);
	        requestObj.setSearch(search);
	        requestObj.setSort(sort);

	        System.out.println("서비스 호출 전");
	        
	        // 서비스 호출
	        Map<String, Object> result = boardService.getBoardListByBoardnum(String.valueOf(boardnum), requestObj);
	        
	        System.out.println("서비스 호출 후 - success: " + result.get("success"));

	        // 응답 반환
	        if ((Boolean) result.get("success")) {
	            return ResponseEntity.ok(result);
	        } else {
	            return ResponseEntity.status(500).body(result);
	        }

	    } catch (Exception e) {
	        System.out.println("게시판 목록 조회 API 오류: " + e.getMessage());
	        e.printStackTrace();

	        Map<String, Object> errorResponse = new HashMap<>();
	        errorResponse.put("success", false);
	        errorResponse.put("message", "서버 오류가 발생했습니다.");
	        errorResponse.put("data", new ArrayList<>());

	        return ResponseEntity.status(500).body(errorResponse);
	    }
	}

    /**
     * 조회수 증가
     */
    @PostMapping("/posts/{id}/view")
    public ResponseEntity<?> increaseViewCount(@PathVariable int id, HttpSession session) {
        System.out.println("=== 조회수 증가 API 호출 ===");
        System.out.println("게시글 ID: " + id);

        try {
            boardService.increaseHits(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "조회수가 증가되었습니다.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("조회수 증가 API 오류: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "조회수 증가 중 오류가 발생했습니다.");

            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
