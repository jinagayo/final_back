package com.spark.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spark.Entity.CommentEntity;
import com.spark.dto.CommentDTO;
import com.spark.dto.CommentRequestDTO;
import com.spark.repository.CommentRepository;

import jakarta.transaction.Transactional;

@Service
public class CommentService {
	
	@Autowired
	private CommentRepository commRepo;
	
	//댓글 조회
    public List<CommentDTO> getCommentsByBoardno(int boardno) {
        List<CommentEntity> allComments = commRepo.findActiveCommentsByBoardno(boardno);
        
        // 최상위 댓글들과 대댓글들을 분리
        Map<Boolean, List<CommentEntity>> partitioned = allComments.stream()
                .collect(Collectors.partitioningBy(comment -> comment.getReno() == 0));
        
        List<CommentEntity> topLevelComments = partitioned.get(true);
        List<CommentEntity> replies = partitioned.get(false);
        
        // 대댓글들을 부모 댓글 ID로 그룹화
        Map<Integer, List<CommentEntity>> repliesMap = replies.stream()
                .collect(Collectors.groupingBy(CommentEntity::getReno));
        
        // 최상위 댓글들을 DTO로 변환하고 대댓글들 추가
        return topLevelComments.stream()
                .map(comment -> {
                    CommentDTO dto = CommentDTO.fromEntity(comment);
                    
                    // 해당 댓글의 대댓글들 추가
                    List<CommentEntity> commentReplies = repliesMap.get(comment.getCommentId());
                    if (commentReplies != null && !commentReplies.isEmpty()) {
                        List<CommentDTO> replyDtos = commentReplies.stream()
                                .map(CommentDTO::fromEntity)
                                .collect(Collectors.toList());
                        dto.setReplies(replyDtos);
                        dto.setReplyCount(replyDtos.size());
                    }
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    //댓글 작성
    public CommentDTO createComment(int boardno, CommentRequestDTO request, String userId) {
        int reno = 0; // 기본값 (최상위 댓글)
        int step = 0; // 기본값
        
        // 대댓글인 경우
        if (request.getParentCommentId() != null && request.getParentCommentId() > 0) {
            // 부모 댓글 존재 확인
            if (!commRepo.existsActiveCommentByIdAndBoardno(request.getParentCommentId(), boardno)) {
                throw new IllegalArgumentException("존재하지 않는 부모 댓글입니다.");
            }
            
            reno = request.getParentCommentId();
            // 해당 부모 댓글의 마지막 step 값 + 1
            step = commRepo.findMaxStepByParentId(reno) + 1;
        }
        
        // 기존 CommentDTO 생성
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setBoardno(boardno);
        commentDTO.setReno(reno);
        commentDTO.setStep(step);
        commentDTO.setContent(request.getContent());
        commentDTO.setCreated_at(new Date());
        commentDTO.setCreated_by(userId);
        commentDTO.setUpdated_at(new Date());
        commentDTO.setUpdated_by(userId);
        commentDTO.setIs_active(1); // 활성 상태
        
        // 기존 CommentEntity 생성자 활용
        CommentEntity commentEntity = new CommentEntity(commentDTO);
        
        CommentEntity savedComment = commRepo.save(commentEntity);
        return CommentDTO.fromEntity(savedComment);
    }
    
    //댓글 수정
    public CommentDTO updateComment(int commentId, CommentRequestDTO request, String userId) {
        CommentEntity comment = commRepo.findActiveCommentByIdAndUser(commentId, userId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없거나 수정 권한이 없습니다."));
        
        comment.setContent(request.getContent());
        comment.setUpdateAt(new Date());
        comment.setUpdateBy(userId);
        
        CommentEntity updatedComment = commRepo.save(comment);
        return CommentDTO.fromEntity(updatedComment);
    }
    
}
