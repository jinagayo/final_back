package com.spark.dto;

import java.sql.Timestamp;

import org.springframework.web.multipart.MultipartFile;

import com.spark.Entity.ClassEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
//강의목록
public class ClassDTO {
	private int classId;//강의코드
	private String name;//강의명
	private String detail;//강의설명
	private int price;//가격
	private String intro;//짧읜 강의 소개(배너)
	private double mark;//평점
	private String teachId;//강사아이디
	private String subId;//과목 코드
	private String img; //대표이미지
	private String state;//강의 승인 반려 대기 여부
	private String updatedBy;
	private String createdBy;
	private Timestamp   createdAt;
	private Timestamp   updatedAt;

	private int lectureCount;  //강의수
	private int qnaCount;    //qna개수
	
	public static ClassDTO fromEntity(ClassEntity entity) {
		ClassDTO dto = new ClassDTO();
		//entity에서 값 복사
		dto.setClassId(entity.getClassId());
		dto.setName(entity.getName());
		dto.setDetail(entity.getDetail());
		dto.setPrice(entity.getPrice());
		dto.setIntro(entity.getIntro());
		dto.setMark(entity.getMark());
		dto.setTeachId(entity.getTeachId());
		dto.setSubId(entity.getSubId());
		dto.setState(entity.getState());
		dto.setCreatedAt(entity.getCreatedAt());
		dto.setUpdatedAt(entity.getUpdatedAt());
		dto.setCreatedBy(entity.getCreatedBy());
		dto.setUpdatedBy(entity.getUpdatedBy());
		
		
		return dto;
	}
	
}