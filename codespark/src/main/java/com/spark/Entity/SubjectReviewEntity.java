package com.spark.Entity;

import com.spark.dto.SubjectReviewDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "subject_review")
@NoArgsConstructor
public class SubjectReviewEntity {
	@Id
	@Column(name = "reviewnum")
	private int reviewnum;//리뷰번호
	
	@Column(name = "class_id")
	private String classId;//강의 코드
	
	@Column(name = "content")
	private String content;//한줄평
	
	@Column(name = "rating")
	private int rating;//별점
	
	public SubjectReviewEntity(SubjectReviewDTO dto) {
		this.reviewnum = dto.getReviewnum();
		this.classId = dto.getClass_id();
		this.content = dto.getContent();
		this.rating = dto.getRating();
	}
}
