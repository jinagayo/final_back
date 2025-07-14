package com.spark.Entity;

import com.spark.dto.SocialPaymentDTO;
import com.spark.dto.StudentDTO;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class StudentEntity {
	private String stud_id;//아이디
	private String degree;//학력
	private String award;//수상이력
	private String skill;//기술스택
	private String license;//자격증
	private String lauguage; // 어학사전
	private String advice; // 취업상담
	
	public StudentEntity(StudentDTO dto) {
		this.stud_id = dto.getStud_id();
		this.degree = dto.getDegree();
		this.award = dto.getAward();
		this.skill = dto.getSkill();
		this.license = dto.getLicense();
		this.lauguage = dto.getLauguage();
		this.advice = dto.getAdvice();
	}
}
