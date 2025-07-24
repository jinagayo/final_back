package com.spark.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
//수강정보
public class AttendanceDTO {
	private int attId;//수강정보id
	private String classId;//강의 코드
	private String stuId;//학생 아이디
	private int price;//결제금액
	private String state;//수강 상태
	private Integer paymentId;//결제 번호
}
