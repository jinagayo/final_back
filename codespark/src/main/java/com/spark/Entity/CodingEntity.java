package com.spark.Entity;

import java.time.LocalDateTime;
import java.util.Date;

import com.spark.dto.CodingDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "coding")
@NoArgsConstructor
public class CodingEntity {
	@Id
	@Column(name = "code_id")
	private int codeId;//코딩문제id
	
	@Column(name = "qeustion")
	private String qeustion;//문제
	
	@Column(name = "type")
	private String type;//문제종류
	
	@Column(name = "level")
	private int level;//난이도
	
	@Column(name = "filed")
	private String filed;//분야
	
	@Column(name = "language")
	private String language;//언어
	
	@Column(name = "test_case")
	private String testCase;//테스트케이스
	
	@Column(name = "model_answer")
	private String modelAnswer;//모범답안
	
	@Column(name = "create_at")
	private LocalDateTime createAt;
	
	@Column(name = "create_by")
	private String createBy;
	
	@Column(name = "update_at")
	private LocalDateTime updateAt;
	
	@Column(name = "update_by")
	private String updateBy;
	
	@Column(name = "is_active")
	private int isActive;
	
	public CodingEntity(CodingDTO dto) {
		this.codeId = dto.getCode_id();
		this.qeustion = dto.getQeustion();
		this.type = dto.getType();
		this.level = dto.getLevel();
		this.filed = dto.getFiled();
		this.language = dto.getLanguage();
		this.testCase = dto.getTest_case();
		this.modelAnswer = dto.getModel_answer();
		this.createAt = dto.getCreate_at();
		this.createBy = dto.getCreate_by();
		this.updateAt = dto.getUpdate_at();
		this.updateBy = dto.getUpdate_by();
		this.isActive = dto.getIs_active();
	}
}
