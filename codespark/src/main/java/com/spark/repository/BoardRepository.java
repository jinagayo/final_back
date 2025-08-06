package com.spark.repository;

import com.spark.Entity.BoardEntity;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Integer> {
    
	/**
	 * 게시판 번호로 게시글 조회 (class_id가 NULL인 것만, 페이징)
	 */
	@Query("SELECT b FROM BoardEntity b WHERE b.boardnum = :boardnum AND b.classId IS NULL")
	Page<BoardEntity> findByBoardnum(@Param("boardnum") String boardnum, Pageable pageable);

	/**
	 * 게시판 번호와 제목/내용 검색 (class_id가 NULL인 것만, 페이징)
	 */
	@Query("SELECT b FROM BoardEntity b WHERE b.boardnum = :boardnum AND b.classId IS NULL AND " +
	       "(LOWER(b.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
	       "LOWER(b.content) LIKE LOWER(CONCAT('%', :search, '%')))")
	Page<BoardEntity> findByBoardnumAndTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
	    @Param("boardnum") String boardnum, 
	    @Param("search") String titleSearch, 
	    @Param("search") String contentSearch, 
	    Pageable pageable);
    
    /**
     * 게시판 번호와 사용자 ID로 게시글 조회 (페이징)
     */
    Page<BoardEntity> findByBoardnumAndUserId(String boardnum, String userId, Pageable pageable);
    
    /**
     * 게시판별 총 게시글 수 조회
     */
    long countByBoardnum(String boardnum);
    
    /**
     * 인기 게시글 조회 (조회수 기준 상위 5개)
     */
    @Query("SELECT b FROM BoardEntity b WHERE b.boardnum = :boardnum ORDER BY b.hits DESC")
    List<BoardEntity> findTop5ByBoardnumOrderByHitsDesc(@Param("boardnum") String boardnum);
    
    /**
     * 최신 게시글 조회 (게시글 ID 기준 상위 5개)
     */
    @Query("SELECT b FROM BoardEntity b WHERE b.boardnum = :boardnum ORDER BY b.boardId DESC")
    List<BoardEntity> findTop5ByBoardnumOrderByBoardIdDesc(@Param("boardnum") String boardnum);
    
    /**
     * 최대 게시글 ID 조회 (자동 증가를 위해)
     */
    @Query("SELECT MAX(b.boardId) FROM BoardEntity b")
    Integer findMaxBoardId();
    
    /**
     * 게시판별 총 조회수 합계
     */
    @Query("SELECT SUM(b.hits) FROM BoardEntity b WHERE b.boardnum = :boardnum")
    Integer sumHitsByBoardnum(@Param("boardnum") String boardnum);
    
    /**
     * 게시판별 최대 조회수
     */
    @Query("SELECT MAX(b.hits) FROM BoardEntity b WHERE b.boardnum = :boardnum")
    Integer findMaxHitsByBoardnum(@Param("boardnum") String boardnum);
    
    /**
     * 특정 사용자의 게시글 수 조회
     */
    long countByBoardnumAndUserId(String boardnum, String userId);
    
    /**
     * 제목으로 게시글 검색
     */
    Page<BoardEntity> findByBoardnumAndTitleContainingIgnoreCase(
        String boardnum, String title, Pageable pageable);
    
    /**
     * 내용으로 게시글 검색
     */
    Page<BoardEntity> findByBoardnumAndContentContainingIgnoreCase(
        String boardnum, String content, Pageable pageable);
    
    /**
     * 작성자로 게시글 검색
     */
    Page<BoardEntity> findByBoardnumAndUserIdContainingIgnoreCase(
        String boardnum, String userId, Pageable pageable);
    
    /**
     * 강의 코드로 게시글 조회
     */
    Page<BoardEntity> findByBoardnumAndClassId(String boardnum, String classId, Pageable pageable);
    
    /**
     * 파일이 첨부된 게시글 조회
     */
    @Query("SELECT b FROM BoardEntity b WHERE b.boardnum = :boardnum AND b.file IS NOT NULL AND b.file != ''")
    Page<BoardEntity> findByBoardnumAndFileIsNotNull(@Param("boardnum") String boardnum, Pageable pageable);
    
    /**
     * 조회수 범위로 게시글 조회
     */
    @Query("SELECT b FROM BoardEntity b WHERE b.boardnum = :boardnum AND b.hits BETWEEN :minHits AND :maxHits")
    Page<BoardEntity> findByBoardnumAndHitsBetween(
        @Param("boardnum") String boardnum, 
        @Param("minHits") int minHits, 
        @Param("maxHits") int maxHits, 
        Pageable pageable);
    
    /**
     * 게시판별 사용자별 게시글 수 통계
     */
    @Query("SELECT b.userId, COUNT(b) FROM BoardEntity b WHERE b.boardnum = :boardnum GROUP BY b.userId ORDER BY COUNT(b) DESC")
    List<Object[]> countByBoardnumGroupByUserId(@Param("boardnum") String boardnum);
    
    /**
     * 게시판별 강의별 게시글 수 통계
     */
    @Query("SELECT b.classId, COUNT(b) FROM BoardEntity b WHERE b.boardnum = :boardnum AND b.classId IS NOT NULL GROUP BY b.classId ORDER BY COUNT(b) DESC")
    List<Object[]> countByBoardnumGroupByClassId(@Param("boardnum") String boardnum);
    
    /**
     * 특정 게시글보다 이전/이후 게시글 찾기 (상세보기에서 이전/다음 버튼용)
     */
    @Query("SELECT b FROM BoardEntity b WHERE b.boardnum = :boardnum AND b.boardId > :boardId ORDER BY b.boardId ASC")
    List<BoardEntity> findNextBoard(@Param("boardnum") String boardnum, @Param("boardId") int boardId);
    
    @Query("SELECT b FROM BoardEntity b WHERE b.boardnum = :boardnum AND b.boardId < :boardId ORDER BY b.boardId DESC")
    List<BoardEntity> findPrevBoard(@Param("boardnum") String boardnum, @Param("boardId") int boardId);

    @Query(value = """
            SELECT 
                b.board_id as id,
                'board' as type,
                b.title as title,
                u.name as author,
                b.boardnum as boardType,
                DATE_FORMAT(b.created_at, '%Y-%m-%d') as date,
                b.hits as views,
                0 as replies,
                SUBSTRING(b.content, 1, 100) as excerpt
            FROM board b 
            JOIN user u ON b.user_id = u.user_id 
            WHERE b.title LIKE CONCAT('%', :keyword, '%') 
               OR b.content LIKE CONCAT('%', :keyword, '%')
               OR u.name LIKE CONCAT('%', :keyword, '%')
            ORDER BY b.created_at DESC
        """, nativeQuery = true)
        List<Map<String, Object>> searchBoards(@Param("keyword") String keyword);

    @Query(value = """
    	    SELECT b.*
    	    FROM board b
    	    WHERE b.is_active = 1
    	      AND b.class_id = :classId
    	      AND b.boardnum = :boardNum
    	      AND (:search = '' OR b.title LIKE CONCAT('%', :search, '%')
    	           OR b.content LIKE CONCAT('%', :search, '%'))
    	    ORDER BY b.created_at DESC
    	    """, 
    	    countQuery = """
    	    SELECT COUNT(*)
    	    FROM board b
    	    WHERE b.is_active = 1
    	      AND b.class_id = :classId
    	      AND b.boardnum = :boardNum
    	      AND (:search = '' OR b.title LIKE CONCAT('%', :search, '%')
    	           OR b.content LIKE CONCAT('%', :search, '%'))
    	    """,
    	    nativeQuery = true)
    	Page<BoardEntity> findBoardsByClassId(
    	    @Param("classId") String classId,
    	    @Param("boardNum") String boardNum,
    	    @Param("search") String search,
    	    @Param("filterBy") String filterBy,
    	    Pageable pageable
    	);
    
    //강의별 게시판 글 작성
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO board (board_id, title, boardnum, user_id, content, class_id, hits, is_active, created_at) " +
                   "VALUES (:boardId, :title, :boardnum, :userId, :content, :classId, 0, 1, NOW())", 
           nativeQuery = true)
    void insertBoardSimple(@Param("boardId") int boardId,
                          @Param("title") String title,
                          @Param("boardnum") String boardnum,
                          @Param("userId") String userId,
                          @Param("content") String content,
                          @Param("classId") String classId);
}