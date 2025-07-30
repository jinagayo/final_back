package com.spark.dto;

import java.sql.Date;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
//코딩문제
public class CodingDTO {
	private int code_id;//코딩문제id
	private String title; // 제목
	private String qeustion;//문제
	private String type;//문제종류
	private int level;//난이도
	private String filed;//분야
	private String language;//언어
	private String test_case;//테스트케이스
	private String model_answer;//모범답안
	private LocalDateTime create_at;// 등록시간
	private String create_by;//등록인
	private LocalDateTime update_at;//수정시간
	private String update_by;//수정인
	private int is_active;//로그용
	
    // 프론트엔드에서 사용하는 추가 필드들
    private double correctRate;    // 정답률
    private int submissions;       // 제출 수
    private boolean isSolved;      // 해결 여부
    private String author;         // 작성자 (create_by와 동일)

}
