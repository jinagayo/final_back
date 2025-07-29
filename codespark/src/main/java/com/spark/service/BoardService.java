package com.spark.service;

import com.spark.Entity.BoardEntity;
import com.spark.dto.BoardDTO;
import com.spark.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class BoardService {
    
    @Autowired
    private BoardRepository boardRepository;
    
    /**
     * 게시판 목록 조회 (검색, 페이징 포함)
     */
    @Transactional(readOnly = true)
    public Page<BoardDTO> getBoardList(String boardnum, String search, Pageable pageable) {
        Page<BoardEntity> entityPage;
        
        // 검색어가 있는 경우
        if (search != null && !search.trim().isEmpty()) {
            search = search.trim();
            entityPage = boardRepository.findByBoardnumAndTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
                boardnum, search, search, pageable);
        } else {
            // 검색어가 없는 경우
            entityPage = boardRepository.findByBoardnum(boardnum, pageable);
        }
        
        // Entity를 DTO로 변환
        return entityPage.map(this::entityToDto);
    }
    
    /**
     * 게시글 상세 조회
     */
    @Transactional(readOnly = true)
    public BoardDTO getBoardById(int boardId) {
        Optional<BoardEntity> entity = boardRepository.findById(boardId);
        return entity.map(this::entityToDto).orElse(null);
    }
    
    /**
     * 조회수 증가
     */
    public void increaseHits(int boardId) {
        BoardEntity entity = boardRepository.findById(boardId)
            .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. ID: " + boardId));
        
        entity.setHits(entity.getHits() + 1);
        boardRepository.save(entity);
    }
    
    /**
     * 게시글 생성
     */
    public BoardDTO createBoard(BoardDTO boardDTO) {
        // 새 게시글의 ID 생성 (자동 증가)
        int nextId = getNextBoardId();
        boardDTO.setBoard_id(nextId);
        
        // 조회수 초기화
        boardDTO.setHits(0);
        
        BoardEntity entity = new BoardEntity(boardDTO);
        BoardEntity savedEntity = boardRepository.save(entity);
        return entityToDto(savedEntity);
    }
    
    /**
     * 게시글 수정
     */
    public BoardDTO updateBoard(BoardDTO boardDTO) {
        BoardEntity existingEntity = boardRepository.findById(boardDTO.getBoard_id())
            .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. ID: " + boardDTO.getBoard_id()));
        
        // 기존 조회수 유지
        boardDTO.setHits(existingEntity.getHits());
        
        BoardEntity entity = new BoardEntity(boardDTO);
        BoardEntity savedEntity = boardRepository.save(entity);
        return entityToDto(savedEntity);
    }
    
    /**
     * 게시글 삭제
     */
    public void deleteBoard(int boardId) {
        if (!boardRepository.existsById(boardId)) {
            throw new RuntimeException("게시글을 찾을 수 없습니다. ID: " + boardId);
        }
        boardRepository.deleteById(boardId);
    }
    
    /**
     * 특정 게시판의 총 게시글 수 조회
     */
    @Transactional(readOnly = true)
    public long getTotalCount(String boardnum) {
        return boardRepository.countByBoardnum(boardnum);
    }
    
    /**
     * 인기 게시글 조회 (조회수 기준)
     */
    @Transactional(readOnly = true)
    public List<BoardDTO> getPopularBoards(String boardnum, int limit) {
        List<BoardEntity> entities = boardRepository.findTop5ByBoardnumOrderByHitsDesc(boardnum);
        return entities.stream()
            .limit(limit)
            .map(this::entityToDto)
            .toList();
    }
    
    /**
     * 최신 게시글 조회
     */
    @Transactional(readOnly = true)
    public List<BoardDTO> getLatestBoards(String boardnum, int limit) {
        List<BoardEntity> entities = boardRepository.findTop5ByBoardnumOrderByBoardIdDesc(boardnum);
        return entities.stream()
            .limit(limit)
            .map(this::entityToDto)
            .toList();
    }
    
    /**
     * 사용자별 게시글 조회
     */
    @Transactional(readOnly = true)
    public Page<BoardDTO> getBoardsByUser(String boardnum, String userId, Pageable pageable) {
        Page<BoardEntity> entityPage = boardRepository.findByBoardnumAndUserId(boardnum, userId, pageable);
        return entityPage.map(this::entityToDto);
    }
    
    /**
     * Entity를 DTO로 변환
     */
    private BoardDTO entityToDto(BoardEntity entity) {
        BoardDTO dto = new BoardDTO();
        dto.setBoard_id(entity.getBoardId());
        dto.setTitle(entity.getTitle());
        dto.setBoardnum(entity.getBoardnum());
        dto.setUser_id(entity.getUserId());
        dto.setContent(entity.getContent());
        dto.setFile(entity.getFile());
        dto.setClass_id(entity.getClassId());
        dto.setHits(entity.getHits());
        return dto;
    }
    
    /**
     * 다음 게시글 ID 생성 (수동 증가)
     */
    private int getNextBoardId() {
        Integer maxId = boardRepository.findMaxBoardId();
        return (maxId != null ? maxId : 0) + 1;
    }
    
    /**
     * 게시판별 통계 정보 조회
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getBoardStatistics(String boardnum) {
        Map<String, Object> stats = new HashMap<>();
        
        long totalCount = boardRepository.countByBoardnum(boardnum);
        Integer totalViews = boardRepository.sumHitsByBoardnum(boardnum);
        Integer maxViews = boardRepository.findMaxHitsByBoardnum(boardnum);
        
        stats.put("totalCount", totalCount);
        stats.put("totalViews", totalViews != null ? totalViews : 0);
        stats.put("maxViews", maxViews != null ? maxViews : 0);
        stats.put("avgViews", totalCount > 0 ? (totalViews != null ? totalViews / (double) totalCount : 0) : 0);
        
        return stats;
    }
}