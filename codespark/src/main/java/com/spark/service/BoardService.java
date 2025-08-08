package com.spark.service;

import com.spark.Entity.BoardEntity;
import com.spark.Entity.ClassEntity;
import com.spark.dto.BoardDTO;
import com.spark.repository.BoardRepository;
import com.spark.repository.CourseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BoardService {
    
    @Autowired
    private BoardRepository boardRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    
    //topbar 검색어 관련
    public List<Map<String, Object>> searchBoards(String keyword, int limit) {
        return boardRepository.searchBoards(keyword);
    }
    
    /**
     * 게시판 목록 조회 (검색, 페이징 포함)
     */
    @Transactional(readOnly = true)
    public Page<BoardDTO> getBoardList(String classId,String boardnum, String search, Pageable pageable) {
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
        boardDTO.setBoard_id(getNextBoardId());
        boardDTO.setHits(0);

        BoardEntity entity = new BoardEntity(boardDTO);
        return entityToDto(boardRepository.save(entity));
    }
    
    
    //강의별 게시글 생성
    public BoardDTO createBoardClassId(String classId, BoardDTO boardDTO) {
        int boardId = getNextBoardId();
        boardDTO.setBoard_id(boardId);
        boardDTO.setClass_id(classId);
        
        System.out.println("BoardDTO boardNum: " + boardDTO.getBoardnum());
        
        // 직접 INSERT
        boardRepository.insertBoardSimple(
            boardId,
            boardDTO.getTitle(),
            boardDTO.getBoardnum(),  // "BOD001" 그대로 저장됨
            boardDTO.getUser_id(),
            boardDTO.getContent(),
            classId,
            boardDTO.getFile()
        );
        
        // 저장된 데이터 조회해서 반환
        BoardEntity savedEntity = boardRepository.findById(boardId).orElse(null);
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
    
    public BoardDTO getBoardByclassId(int boardId) {
        return getBoardById(boardId); // 기존 메서드 재사용
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
    
    //강의별 게시판 조회
    public Page<Map<String, Object>> getBoardsByClassId(String classId, String boardNum, String search, String filterBy, Pageable pageable) {
        try {
            
            Page<BoardEntity> boardPage = boardRepository.findBoardsByClassId(classId, boardNum, search, filterBy, pageable);
            
            List<Map<String, Object>> boardList = boardPage.getContent().stream()
                .map(this::convertBoardToMap)
                .collect(Collectors.toList());
            
            return new PageImpl<>(boardList, pageable, boardPage.getTotalElements());
            
        } catch (Exception e) {
            System.err.println("Service - 강의별 게시글 조회 오류: " + e.getMessage());
            throw new RuntimeException("강의별 게시글 조회 실패", e);
        }
    }
		 
 // 강의별 게시판 수정
//    public BoardDTO getBoardByclassId(int boardId) {
//        BoardEntity entity = boardRepository.findById(boardId)
//            .orElse(null);
//        
//        if (entity == null) {
//            return null;
//        }
//        
//        return entityToDto(entity);
//    }
    
    public BoardDTO updateBoardByClassId(BoardDTO boardDTO) {
        // 1. 기존 엔티티 조회
        BoardEntity existingEntity = boardRepository.findById(boardDTO.getBoard_id())
            .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. ID: " + boardDTO.getBoard_id()));

        // 2. 디버깅 출력 (기존 값 확인)
        System.out.println("=== 수정 전 값 확인 ===");
        System.out.println("수정 전 classId: " + existingEntity.getClassId());
        System.out.println("수정 전 is_active: " + existingEntity.getIsActive()); // 🔥 추가
        System.out.println("DTO에서 받은 is_active: " + boardDTO.getIs_active()); // 🔥 추가

        // 3. 수정할 필드만 변경
        existingEntity.setTitle(boardDTO.getTitle());
        existingEntity.setContent(boardDTO.getContent());
        existingEntity.setFile(boardDTO.getFile());
        existingEntity.setUpdateAt(new Date());
        existingEntity.setUpdateBy(boardDTO.getUpdated_by());

        // 4. classId는 DTO에 값이 있는 경우에만 업데이트
        if (boardDTO.getClass_id() != null) {
            existingEntity.setClassId(boardDTO.getClass_id());
        }
        
        // 🔥 5. is_active 값 유지 - 이 부분이 핵심!
        if (boardDTO.getIs_active() != -1) { // -1은 "설정 안됨"을 의미
            existingEntity.setIsActive(boardDTO.getIs_active());
            System.out.println("is_active 값을 DTO에서 설정: " + boardDTO.getIs_active());
        } else {
            System.out.println("is_active 값을 기존 값으로 유지: " + existingEntity.getIsActive());
        }

        // 🔥 6. 다른 중요한 필드들도 유지
        if (boardDTO.getUser_id() != null) {
            existingEntity.setUserId(boardDTO.getUser_id());
        }
        if (boardDTO.getCreated_by() != null) {
            existingEntity.setCreateBy(boardDTO.getCreated_by());
        }
        if (boardDTO.getBoardnum() != null) {
            existingEntity.setBoardnum(boardDTO.getBoardnum());
        }

        // 7. 저장
        BoardEntity savedEntity = boardRepository.save(existingEntity);

        // 8. 디버깅 출력 (변경 후 확인)
        System.out.println("=== 수정 후 값 확인 ===");
        System.out.println("수정 후 classId: " + savedEntity.getClassId());
        System.out.println("수정 후 is_active: " + savedEntity.getIsActive()); // 🔥 추가

        // 9. DTO 변환 후 반환
        return entityToDto(savedEntity);
    }
    
    private Map<String, Object> convertBoardToMap(BoardEntity board) {
    	Map<String, Object> map = new HashMap<>();
        map.put("id", board.getBoardId());
        map.put("title", board.getTitle());
        map.put("content", board.getContent());
        map.put("author", board.getCreateBy());
		map.put("createBy", board.getCreateBy());
		map.put("createdAt", board.getCreateAt());
		map.put("updatedAt", board.getUpdateAt());
		map.put("viewCount", board.getHits());
		map.put("views", board.getHits());
		map.put("hits", board.getHits());
		map.put("classId", board.getClassId());
		map.put("boardnum", board.getBoardnum());
		map.put("userId", board.getUserId());
		map.put("file", board.getFile());
		map.put("isActive", board.getIsActive());
        
		// 고정 게시글 여부 (필요시 로직 추가)
		map.put("isPinned", false);
		map.put("pinned", false);
		return map;
    }

    public void deleteBoardByClassId(String classId, int boardId, String userId) {
        BoardEntity board = boardRepository.findById(boardId)
            .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. ID: " + boardId));
        
        if (!userId.equals(board.getUserId())) {
            throw new RuntimeException("삭제 권한이 없습니다. 작성자만 삭제할 수 있습니다.");
        }
        
        if (!classId.equals(board.getClassId())) {
            throw new RuntimeException("해당 강의의 게시글이 아닙니다.");
        }
        
        boardRepository.deleteById(boardId);
        
        System.out.println("게시글 삭제 완료 - boardId: " + boardId + ", userId: " + userId);
    }

	public BoardEntity findById(int boardId) {
		 return boardRepository.findById(boardId).orElse(null);
	}

}