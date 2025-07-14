package com.spark.Entity;

import com.spark.dto.CommentDTO;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class CommentEntity {
	private int comment_id;//댓글 아이디
	private int boardno; //게시글번호
	private int reno; //부모댓글
	private int step; //순서
	private String content; //내용
	
	public CommentEntity(CommentDTO dto) {
		this.comment_id = dto.getComment_id();
		this.boardno = dto.getBoardno();
		this.reno = dto.getReno();
		this.step = dto.getStep();
		this.content = dto.getContent();
	}
}
