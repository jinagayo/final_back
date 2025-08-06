package com.spark.Entity;

import java.util.Date;

import com.spark.dto.BoardDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "board")
@NoArgsConstructor
public class BoardEntity {

	@Id	
	@Column(name = "board_id")
	private int boardId; //게시판id(글번호)
	
	@Column(name = "title") 
	private String title;//제목
	
	@Column(name = "boardnum")
	private String boardnum; //게시판번호
	
	@Column(name = "user_id")
	private String userId;//작성자 아이디
	
	@Column(name = "content")
	private String content;//내용
	
	@Column(name = "file")
	private String file;//파일
	
	@Column(name = "class_id")
	private String classId;//강의코드
	
	@Column(name = "hits")
	private int hits;//조회수
	
	@Column(name = "created_at")
	private Date createAt;
	
	@Column(name = "created_by")
	private String createBy;
	
	@Column(name = "updated_at")
	private Date updateAt;
	
	@Column(name = "updated_by")
	private String updateBy;
	
	@Column(name = "is_active")
	private int isActive;
	
	public BoardEntity(BoardDTO dto) {
		this.boardId = dto.getBoard_id();
		this.title = dto.getTitle();
		this.boardnum = dto.getBoardnum();
		this.userId = dto.getUser_id();
		this.content = dto.getContent();
		this.file = dto.getFile();
		this.classId = dto.getClass_id();
		this.hits = dto.getHits();
		this.createAt = dto.getCreated_at();
		this.createBy = dto.getCreated_by();
		this.updateAt = dto.getUpdated_at();
		this.updateBy = dto.getUpdated_by();
		this.isActive = dto.getIs_active();
	}
}
