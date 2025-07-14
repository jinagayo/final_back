package com.spark.Entity;

import com.spark.dto.SubjectReviewDTO;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class SubjectReviewEntity {
	private int reviewnum;//리뷰번호
	private String class_id;//강의 코드
	private String content;//한줄평
	private int rating;//별점
	
	public SubjectReviewEntity(SubjectReviewDTO dto) {
		this.reviewnum = dto.getReviewnum();
		this.class_id = dto.getClass_id();
		this.content = dto.getContent();
		this.rating = dto.getRating();
	}
}
