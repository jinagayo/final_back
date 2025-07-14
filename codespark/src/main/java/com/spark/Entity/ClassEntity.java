package com.spark.Entity;

import com.spark.dto.ClassDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ClassEntity {
	@Id
	private String class_id;//강의코드
	private String name;//강의명
	private String detail;//강의설명
	private int price;//가격
	private String intro;//짧읜 강의 소개(배너)
	private double mark;//평점
	private String teach_id;//강사아이디
	private String sub_id;//과목 코드
	private String img;//대표이미지
	
	public ClassEntity(ClassDTO dto) {
		this.class_id = dto.getClass_id();
		this.name = dto.getName();
		this.detail = dto.getDetail();
		this.price = dto.getPrice();
		this.intro = dto.getIntro();
		this.mark = dto.getMark();
		this.teach_id = dto.getTeach_id();
		this.sub_id = dto.getSub_id();
		this.img = dto.getImg();
	}
}
