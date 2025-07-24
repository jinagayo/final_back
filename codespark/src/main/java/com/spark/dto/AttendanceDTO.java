package com.spark.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
//수강정보
public class AttendanceDTO {
	private String att_id;//수강정보id
	private String class_id;//강의 코드
	private String stu_id;//학생 아이디
	private int price;//결제금액
	private String state;//수강 상태
	private String payment_id;//결제 번호
}
