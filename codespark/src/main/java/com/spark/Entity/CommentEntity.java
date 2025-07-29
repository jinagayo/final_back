package com.spark.Entity;

import java.util.Date;

import com.spark.dto.CommentDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "comment")
@NoArgsConstructor
public class CommentEntity {
	@Id
	@Column(name = "comment_id")
	private int commentId;//댓글 아이디
	
	@Column(name = "boardno")
	private int boardno; //게시글번호
	
	@Column(name = "reno")
	private int reno; //부모댓글
	
	@Column(name = "step")
	private int step; //순서
	
	@Column(name = "content")
	private String content; //내용
	
	@Column(name = "created_at")
	private Date createAt;
	
	@Column(name = "created_by")
	private String createBy;
	
	@Column(name = "updated_at")
	private Date updateAt;
	
	@Column(name = "update_by")
	private String updateBy;
	
	@Column(name = "is_active")
	private int isActive;
	
	public CommentEntity(CommentDTO dto) {
		this.commentId = dto.getComment_id();
		this.boardno = dto.getBoardno();
		this.reno = dto.getReno();
		this.step = dto.getStep();
		this.content = dto.getContent();
		this.createAt = dto.getCreated_at();
		this.createBy = dto.getCreated_by();
		this.updateAt = dto.getUpdated_at();
		this.updateBy = dto.getUpdated_by();
		this.isActive = dto.getIs_active();
	}
}
