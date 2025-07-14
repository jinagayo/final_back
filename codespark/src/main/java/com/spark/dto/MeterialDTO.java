package com.spark.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
//강의 자료
public class MeterialDTO {
	private int meter_id;//강의자료id
	private String class_id;//강의 코드
	private int seq;//순서
	private String content;//내용
	private String type;//종류(영상,과제)
	private int time;//동영상길이
}
