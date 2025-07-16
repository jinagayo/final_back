package com.spark.Entity;

import com.spark.dto.TestDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "test")
@NoArgsConstructor
public class TestEntity {
	@Id
	@Column(name = "test_id")
	private int testId;//테스트문제id
	
	@Column(name = "meterial_id")
	private int meterialId;//자료번호
	
	@Column(name = "probno")
	private int probno;//문제번호
	
	@Column(name = "question")
	private String question;//문제
	
	@Column(name = "type")
	private String type;//객관식/주관식
	
	@Column(name = "choice1")
	private String choice1;//선지
	
	@Column(name = "choice2")
	private String choice2;//선지
	
	@Column(name = "choice3")
	private String choice3;//선지
	
	@Column(name = "choice4")
	private String choice4;//선지
	
	@Column(name = "answer")
	private String answer;//정답
	
	public TestEntity(TestDTO dto) {
		this.testId = dto.getTest_id();
		this.meterialId = dto.getMeterial_id();
		this.probno = dto.getProbno();
		this.question = dto.getQuestion();
		this.type = dto.getType();
		this.choice1 = dto.getChoice1();
		this.choice2 = dto.getChoice2();
		this.choice3 = dto.getChoice3();
		this.choice4 = dto.getChoice4();
		this.answer = dto.getAnswer();
	}
}
