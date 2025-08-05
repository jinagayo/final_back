package com.spark.dto;


import java.sql.Timestamp;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter
@ToString
public class ClassInfoDTO {
	private int classId;//강의코드
	private String name;//강의명
	private String detail;//강의설명
	private int price;//가격
	private String intro;//짧읜 강의 소개(배너)
	private double mark;//평점
	private String img; //대표이미지
	private String teacher;//강사명
	private String subject;//과목명
	private String state;//강의 승인 반려 대기 여부
	private Timestamp createdAt;
	private Timestamp updatedAt;

	private String updatedBy;
	private String createdBy;
	private int lectureCount;
	private int qnaCount;
	private int studentCount;
	
	
    public ClassInfoDTO(int classId, String name, String detail, int price, String intro,
            double mark, String img, String teacher, String subject, String state,Timestamp createdAt,
            Timestamp updatedAt, String updatedBy, String createdBy) {
	this.classId = classId;
	this.name = name;
	this.detail = detail;
	this.price = price;
	this.intro = intro;
	this.mark = mark;
	this.img = img;
	this.teacher = teacher;
	this.subject = subject;
	this.state = state;
	this.createdAt=createdAt;
	this.updatedAt=updatedAt;
	this.updatedBy=updatedBy;
	this.createdBy=createdBy;

	}

	public ClassInfoDTO() {
		// TODO Auto-generated constructor stub
	}
}
