package com.spark.repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spark.Entity.BoardEntity;
import com.spark.Entity.CodingEntity;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity,Integer>{
	@Modifying
	@Query("UPDATE BoardEntity b SET b.hits = b.hits + 1 WHERE b.boardId = :boardId")
	void increaseHits(int boardId);

    @Query(value = "SELECT COUNT(*) FROM board b WHERE " +
            "b.boardnum = :boardnum AND " +
            "(:search = '' OR b.title LIKE CONCAT('%', :search, '%') OR " +
            "b.content LIKE CONCAT('%', :search, '%') OR " +
            "b.user_id LIKE CONCAT('%', :search, '%'))",
    nativeQuery = true)
	long countByBoardnum(@Param("boardnum")String boardnum,@Param("search")String search);

	
    @Query(value = "SELECT * FROM board b WHERE " +
            "b.boardnum = :boardnum AND " +
            "(:search = '' OR b.title LIKE CONCAT('%', :search, '%') OR " +
            "b.content LIKE CONCAT('%', :search, '%') OR " +
            "b.user_id LIKE CONCAT('%', :search, '%')) " +
            "ORDER BY " +
            "CASE WHEN :sort = 'latest' THEN b.board_id END DESC, " +
            "CASE WHEN :sort = 'oldest' THEN b.board_id END ASC, " +
            "CASE WHEN :sort = 'views' THEN b.hits END DESC",
            nativeQuery = true)
    List<BoardEntity> findByBoardnum(@Param("boardnum") String boardnum, 
            @Param("search") String search, 
            @Param("sort") String sort, 
            Pageable pageable);
	
}
