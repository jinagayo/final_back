package com.spark.Entity;

import com.spark.dto.AttendanceDTO;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Data
@NoArgsConstructor
public class AttendanceEntity {
	private int att_id;//수강정보id
	private String class_id;//강의 코드
	private String stu_id;//학생 아이디
	private int price;//결제금액
	private String state;//수강 상태
	private int reviewnum;//리뷰 번호
	private int payment_id;//결제 번호
	
	public AttendanceEntity(AttendanceDTO dto) {
		this.att_id = dto.getAtt_id();
		this.class_id = dto.getClass_id();
		this.stu_id = dto.getStu_id();
		this.price = dto.getPrice();
		this.state = dto.getState();
		this.reviewnum = dto.getReviewnum();
		this.payment_id = dto.getPayment_id();
	}
}
