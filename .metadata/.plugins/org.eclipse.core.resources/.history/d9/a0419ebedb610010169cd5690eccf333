package com.spark.Entity;

import com.spark.dto.MeterialDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class MeterialEntity {
	@Id
	private int meter_id;//강의자료id
	private String class_id;//강의 코드
	private int seq;//순서
	private String content;//내용
	private String type;//종류(영상,과제)
	private int time;//동영상길이
	
	public MeterialEntity(MeterialDTO dto) {
		this.meter_id = dto.getMeter_id();
		this.class_id = dto.getClass_id();
		this.seq = dto.getSeq();
		this.content = dto.getContent();
		this.type = dto.getType();
		this.time = dto.getTime();
	}
}
