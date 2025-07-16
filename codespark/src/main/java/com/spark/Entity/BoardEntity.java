package com.spark.Entity;

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
	
	public BoardEntity(BoardDTO dto) {
		this.boardId = dto.getBoard_id();
		this.title = dto.getTitle();
		this.boardnum = dto.getBoardnum();
		this.userId = dto.getUser_id();
		this.content = dto.getContent();
		this.file = dto.getFile();
		this.classId = dto.getClass_id();
		this.hits = dto.getHits();
	}
}
