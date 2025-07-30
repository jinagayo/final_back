package com.spark.dto;

import com.spark.Entity.ClassEntity;
import com.spark.Entity.MeterialEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
//강의 자료
public class MeterialDTO {
	private int meterId;//강의자료id
	private String classId;//강의 코드
	private int seq;//순서
	private String content;//내용
	private String type;//종류(영상,과제)
	private int time;//동영상길이
	
	public static MeterialDTO fromEntity(MeterialEntity entity) {
	    MeterialDTO dto = new MeterialDTO();
	    dto.setMeterId(entity.getMeterId());
	    dto.setClassId(entity.getClassId());
	    dto.setSeq(entity.getSeq());
	    dto.setContent(entity.getContent());
	    dto.setType(entity.getType());
	    dto.setTime(entity.getTime());
	    return dto;
	}

}
