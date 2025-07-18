package com.spark.Entity;

import com.spark.dto.BoardDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class BoardEntity {
	@Id
	private int board_id; //게시판id(글번호)
	private String title;//제목
	private String boardnum; //게시판번호
	private String user_id;//작성자 아이디
	private String content;//내용
	private String file;//파일
	private String class_id;//강의코드
	private int hits;//조회수
	
	public BoardEntity(BoardDTO dto) {
		this.board_id = dto.getBoard_id();
		this.title = dto.getTitle();
		this.boardnum = dto.getBoardnum();
		this.user_id = dto.getUser_id();
		this.content = dto.getContent();
		this.file = dto.getFile();
		this.class_id = dto.getClass_id();
		this.hits = dto.getHits();
	}
}
