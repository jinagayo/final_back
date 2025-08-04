package com.spark.Entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.spark.dto.TestSubDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "test_sub")
@SQLDelete(sql = "UPDATE test_sub SET is_active = false WHERE class_id = ?")
@Where(clause = "is_active = true")
@NoArgsConstructor
public class TestSubEntity {
	@Id
	@Column(name = "testsub_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int testsubId;//테스트풀이id
	
	@Column(name = "testnum")
	private int testnum;//문제번호
	
	@Column(name = "stud_id")
	private String studId;//푼놈
	
	@Column(name = "submit")
	private String submit;//제출한답
	
	@Column(name = "correct")
	private boolean correct;//정답여부
	
	public TestSubEntity(TestSubDTO dto) {
		this.testsubId = dto.getTestsub_id();
		this.testnum = dto.getTestnum();
		this.submit = dto.getSubmit();
		this.correct = dto.isCorrect();
		this.studId = dto.getStudId();
	}
}
