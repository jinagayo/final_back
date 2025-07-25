package com.spark.dto;

import com.spark.Entity.ClassEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
//강의목록
public class ClassDTO {
	private String class_id;//강의코드
	private String name;//강의명
	private String detail;//강의설명
	private int price;//가격
	private String intro;//짧읜 강의 소개(배너)
	private double mark;//평점
	private String teach_id;//강사아이디
	private String sub_id;//과목 코드
	private String img;//대표이미지

	private int lectureCount;  //강의수
	private int qnaCount;    //qna개수
	private int studentCount;  //수강생 수
	
	public static ClassDTO fromEntity(ClassEntity entity) {
		ClassDTO dto = new ClassDTO();
		//entity에서 값 복사
		dto.setClass_id(entity.getClassId());
		dto.setName(entity.getName());
		dto.setDetail(entity.getDetail());
		dto.setPrice(entity.getPrice());
		dto.setIntro(entity.getIntro());
		dto.setMark(entity.getMark());
		dto.setTeach_id(entity.getTeachId());
		dto.setSub_id(entity.getSubId());
		dto.setImg(entity.getImg());
		
		return dto;
	}
	
}