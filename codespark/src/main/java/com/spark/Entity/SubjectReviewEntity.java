package com.spark.Entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.spark.dto.SubjectReviewDTO;

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
@Table(name = "subject_review")
@SQLDelete(sql = "UPDATE subject_review SET is_active = false WHERE class_id = ?")
@Where(clause = "is_active = true")
@NoArgsConstructor
public class SubjectReviewEntity {
	@Id
	@Column(name = "reviewnum")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int reviewnum;//리뷰번호
	
	@Column(name = "class_id")
	private String classId;//강의 코드
	
	@Column(name = "content")
	private String content;//한줄평
	
	@Column(name = "rating")
	private int rating;//별점
	

	@Column(name = "att_id")
	private int attId;//attendacnce 번호
	
	
	public SubjectReviewEntity(SubjectReviewDTO dto) {
		this.reviewnum = dto.getReviewnum();
		this.classId = dto.getClass_id();
		this.content = dto.getContent();
		this.rating = dto.getRating();
		this.attId = dto.getAttId();
	}
}
