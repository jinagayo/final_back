package com.spark.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spark.Entity.CommentEntity;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {

    @Query("SELECT c FROM CommentEntity c WHERE c.boardno = :boardno AND c.isActive = 1 " +
            "ORDER BY CASE WHEN c.reno = 0 THEN c.commentId ELSE c.reno END ASC, " +
            "c.step ASC, c.commentId ASC")
     List<CommentEntity> findActiveCommentsByBoardno(@Param("boardno") int boardno);
    
    @Query("SELECT COUNT(c) > 0 FROM CommentEntity c WHERE c.commentId = :commentId AND c.boardno = :boardno AND c.isActive = 1")
    boolean existsActiveCommentByIdAndBoardno(@Param("commentId") int commentId, @Param("boardno") int boardno);
    
    @Query("SELECT COALESCE(MAX(c.step), 0) FROM CommentEntity c WHERE c.reno = :parentId")
    Integer findMaxStepByParentId(@Param("parentId") int parentId);
    
    @Query("SELECT c FROM CommentEntity c WHERE c.commentId = :commentId AND c.createBy = :userId AND c.isActive = 1")
    Optional<CommentEntity> findActiveCommentByIdAndUser(@Param("commentId") int commentId, @Param("userId") String userId);
	
}
