package com.spark.Entity;

import com.spark.dto.CodingDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class CodingEntity {
	@Id
	private int code_id;//코딩문제id
	private String qeustion;//문제
	private String type;//문제종류
	private int level;//난이도
	private String filed;//분야
	private String language;//언어
	private String test_case;//테스트케이스
	private String model_answer;//모범답안
	
	public CodingEntity(CodingDTO dto) {
		this.code_id = dto.getCode_id();
		this.qeustion = dto.getQeustion();
		this.type = dto.getType();
		this.level = dto.getLevel();
		this.filed = dto.getFiled();
		this.language = dto.getLanguage();
		this.test_case = dto.getTest_case();
		this.model_answer = dto.getModel_answer();
	}
}
