package com.spark.dto;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
//게시판
public class BoardDTO {
	private int board_id; //게시판id(글번호)
	private String title;//제목
	private String boardnum; //게시판번호
	private String user_id;//작성자 아이디
	private String content;//내용
	private String file;//파일
	private String class_id;//강의코드
	private int hits;//조회수
	private Date created_at;//등록날짜
	private String created_by;//등록인
	private Date updated_at;//수정날짜
	private String updated_by;//수정인
	private int is_active;//활성
}
