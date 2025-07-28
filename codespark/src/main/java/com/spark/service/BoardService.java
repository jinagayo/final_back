package com.spark.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spark.Entity.BoardEntity;
import com.spark.controller.AdminController;
import com.spark.controller.AuthController;
import com.spark.controller.BoardController;
import com.spark.dto.BoardDTO;
import com.spark.dto.BoardSearchRequest;
import com.spark.repository.BoardRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class BoardService {
	@Autowired
	private BoardRepository boardRepo;

    public Map<String, Object> getBoardListByBoardnum(String boardnum, BoardSearchRequest request) {
        try {
            System.out.println("===게시판 목록 조회===");
            System.out.println("boardnum: " + boardnum);

            // Pageable 객체 생성
            Sort sort = createSort(request.getSort());
            Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), sort);

            // 데이터 조회
            List<BoardEntity> boardList = boardRepo.findByBoardnum(boardnum, request.getSearch(), request.getSort(), pageable);
            
            // 전체 개수 조회
            long totalElement = boardRepo.countByBoardnum(boardnum, request.getSearch());
            int totalPage = (int) Math.ceil((double) totalElement / request.getSize());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", boardList);
            response.put("boardnum", boardnum);
            response.put("currentPage", request.getPage());
            response.put("totalPage", totalPage);
            response.put("size", request.getSize());
            response.put("hasNext", request.getPage() < totalPage);
            response.put("hasPrevious", request.getPage() > 1);

            System.out.println("조회 결과: " + boardList.size() + "개");
            return response;

        } catch (Exception e) {
            System.out.println("게시판 조회 중 오류: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "게시판 조회 중 오류가 발생했습니다: " + e.getMessage());
            errorResponse.put("data", new ArrayList<>());
            return errorResponse;
        }
    }

    private Sort createSort(String sortType) {
        switch (sortType) {
            case "latest":
                return Sort.by(Sort.Direction.DESC, "boardId");
            case "oldest":
                return Sort.by(Sort.Direction.ASC, "boardId");
            case "views":
                return Sort.by(Sort.Direction.DESC, "hits");
            default:
                return Sort.by(Sort.Direction.DESC, "boardId");
        }
    }
	
	@Transactional
	public void increaseHits(int boardId) {
		try {
			boardRepo.increaseHits(boardId);
		}catch(Exception e) {
			System.out.println("조회수 증가 중 오류" + e.getMessage());
		}
	}
	
	private String getBoardName(String boardnum) {
		switch(boardnum) {
		case "1" : return "공지사항";
		case "2" : return "자유게시판";
		case "3" : return "QnA";
		}
		return "게시판";
	}
	
	
}
