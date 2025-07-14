package com.spark.Entity;

import com.spark.dto.TestSubDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class TestSubEntity {
	@Id
	private int testsub_id;//테스트풀이id
	private int testnum;//문제번호
	private String submit;//제출한답
	private boolean correct;//정답여부
	
	public TestSubEntity(TestSubDTO dto) {
		this.testsub_id = dto.getTestsub_id();
		this.testnum = dto.getTestnum();
		this.submit = dto.getSubmit();
		this.correct = dto.isCorrect();
	}
}
