package com.spark.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Data
//자료 수행
public class MeterialSubDTO {
	private int metersub_id;//자료수행id
	private int meterial_id;//자료번호
	private String content;//과제물/메모
	private int progress;//진행률
}
