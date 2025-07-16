package com.spark.Entity;

import com.spark.dto.CommonDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "common")
@NoArgsConstructor
public class CommonEntity {
	@Id
	@Column(name = "com_id")
	private String comId;//공통테이블id
	
	@Column(name = "name")
	private String name;//코드명
	
	@Column(name = "number")
	private int number;//순서
	
	public CommonEntity(CommonDTO dto) {
		this.comId = dto.getCom_id();
		this.name = dto.getName();
		this.number = dto.getNumber();
	}
}
