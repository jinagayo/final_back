package com.spark.dto;

import com.spark.Entity.MeterialEntity;

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
	private String title;
	private long time;//동영상길이
	private String detail;
	
	public static MeterialDTO fromEntity(MeterialEntity entity) {
		MeterialDTO dto = new MeterialDTO();
		dto.setClass_id(entity.getClassId());
		dto.setMeter_id(entity.getMeterId());
		dto.setSeq(entity.getSeq());
		dto.setContent(entity.getContent());
		dto.setType(entity.getType());
		dto.setTitle(entity.getTitle());
		dto.setTime(entity.getTime());
		dto.setDetail(entity.getDetail());
		
		return dto;
	}

	public MeterialDTO(int meter_id2, String content2) {
		meter_id = meter_id2;
		content = content2;
	}

	public MeterialDTO() {
		
	}
}
