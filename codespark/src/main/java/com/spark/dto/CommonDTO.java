package com.spark.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
//공통테이블
public class CommonDTO {
	private String com_id;//공통테이블id
	private String name;//코드명
	private int number;//순서
}
