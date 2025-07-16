package com.spark.Entity;

import com.spark.dto.CommonDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class CommonEntity {
	@Id
	private String com_id;//공통테이블id
	private String name;//코드명
	private int number;//순서
	
	public CommonEntity(CommonDTO dto) {
		this.com_id = dto.getCom_id();
		this.name = dto.getName();
		this.number = dto.getNumber();
	}
}
