package com.spark.Entity;

import com.spark.dto.MeterialDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "meterial")
@NoArgsConstructor
@Data
public class MeterialEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "meter_id")
	private int meterId;//강의자료id
	
	@Column(name = "class_id")
	private int classId;//강의 코드
	
	@Column(name = "seq")
	private int seq;//순서
	
	@Column(name = "content")
	private String content;//내용
	
	@Column(name = "type")
	private String type;//종류(영상,과제)
	
	@Column(name = "time")
	private int time;
	
	@Column(name = "title")
	private String title;//제목

	

	@Column(name = "detail")
	private String detail;
	
	public MeterialEntity(MeterialDTO dto) {
		this.meterId = dto.getMeterId();
		this.classId = dto.getClassId();
		this.seq = dto.getSeq();
		this.content = dto.getContent();
		this.type = dto.getType();
		this.time = dto.getTime();
		this.title = dto.getTitle();
		this.detail = dto.getDetail(); 
	}
}
