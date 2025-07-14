package com.spark.Entity;

import com.spark.dto.MeterialSubDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class MeterialSubEntity {
	@Id
	private int metersub_id;//자료수행id
	private int meterial_id;//자료번호
	private String content;//과제물/메모
	private int progress;//진행률
	
	public MeterialSubEntity(MeterialSubDTO dto) {
		this.metersub_id = dto.getMetersub_id();
		this.meterial_id = dto.getMeterial_id();
		this.content = dto.getContent();
		this.progress = dto.getProgress();
	}
}
