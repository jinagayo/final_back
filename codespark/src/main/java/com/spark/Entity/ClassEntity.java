package com.spark.Entity;

import org.hibernate.annotations.Where;
import org.springframework.web.multipart.MultipartFile;

import com.spark.dto.ClassDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;


@Entity
@Data
@Table(name = "class")
@SQLDelete(sql = "UPDATE class SET is_active = false WHERE class_id = ?")
@Where(clause = "is_active = true")
@NoArgsConstructor
public class ClassEntity {
	@Id
	@Column(name = "class_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int classId;//강의코드

	@Column(name = "is_active")
    private boolean isActive = true;
	
	@Column(name = "name")
	private String name;//강의명
	
	@Column(name = "detail")
	private String detail;//강의설명
	
	@Column(name = "price")
	private int price;//가격
	
	@Column(name = "intro")
	private String intro;//짧읜 강의 소개(배너)
	
	@Column(name = "mark")
	private double mark;//평점
	
	@Column(name = "teach_id")
	private String teachId;//강사아이디
	
	@Column(name = "sub_id")
	private String subId;//과목 코드
	
	@Column(name = "img")
	private String img; //대표이미지
	
	@Column(name="state")
	private String state;
	
	@Column(name = "created_at", insertable = false, updatable = false)
	private Timestamp createdAt;

	@Column(name = "updated_at", insertable = false, updatable = false)
	private Timestamp updatedAt;

	
	@Column(name="updated_by")
	private String updatedBy;
	
	@Column(name="created_by")
	private String createdBy;

	
	public ClassEntity(ClassDTO dto) {
		this.classId = dto.getClassId();
		this.name = dto.getName();
		this.detail = dto.getDetail();
		this.price = dto.getPrice();
		this.intro = dto.getIntro();
		this.mark = dto.getMark();
		this.teachId = dto.getTeachId();
		this.subId = dto.getSubId();
		this.img = dto.getImg();
		this.state = dto.getState();
		this.createdAt=dto.getCreatedAt();
		this.updatedAt=dto.getUpdatedAt();
		this.createdBy=dto.getCreatedBy();
		this.updatedBy=dto.getUpdatedBy();
	}
}