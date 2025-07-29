package com.spark.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.spark.Entity.CommentEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
//댓글
public class CommentDTO {
	private int comment_id;//댓글 아이디
	private int boardno; //게시글번호
	private int reno; //부모댓글
	private int step; //순서
	private String content; //내용
	private Date created_at;//작성시간
	private String created_by;//작성자
	private Date updated_at;//수정시간
	private String updated_by;//수정자
	private int is_active;//활성화
	
    // 프론트엔드용 추가 필드 (DB에는 없음)
    private List<CommentDTO> replies = new ArrayList<>(); // 대댓글 목록
    private int replyCount = 0; // 대댓글 개수
    
    // 기본 생성자
    public CommentDTO() {
        this.replies = new ArrayList<>();
        this.replyCount = 0;
        this.is_active = 1; // 기본값: 활성
    }
    
    // Entity에서 DTO로 변환
    public static CommentDTO fromEntity(CommentEntity entity) {
        CommentDTO dto = new CommentDTO();
        dto.setComment_id(entity.getCommentId());
        dto.setBoardno(entity.getBoardno());
        dto.setReno(entity.getReno());
        dto.setStep(entity.getStep());
        dto.setContent(entity.getContent());
        dto.setCreated_at(entity.getCreateAt());
        dto.setCreated_by(entity.getCreateBy());
        dto.setUpdated_at(entity.getUpdateAt());
        dto.setUpdated_by(entity.getUpdateBy());
        dto.setIs_active(entity.getIsActive());
        return dto;
    }
    
    // 최상위 댓글인지 확인
    public boolean isTopLevel() {
        return reno == 0;
    }
    
    // 대댓글인지 확인
    public boolean isReply() {
        return reno > 0;
    }
    
    // 활성 상태인지 확인
    public boolean isActive() {
        return is_active == 1;
    }
}
