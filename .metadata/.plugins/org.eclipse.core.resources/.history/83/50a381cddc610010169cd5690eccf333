package com.spark.Entity;

import com.spark.dto.TestDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class TestEntity {
	@Id
	private int test_id;//테스트문제id
	private int meterial_id;//자료번호
	private int probno;//문제번호
	private String question;//문제
	private String type;//객관식/주관식
	private String choice1;//선지
	private String choice2;//선지
	private String choice3;//선지
	private String choice4;//선지
	private String answer;//정답
	
	public TestEntity(TestDTO dto) {
		this.test_id = dto.getTest_id();
		this.meterial_id = dto.getMeterial_id();
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
