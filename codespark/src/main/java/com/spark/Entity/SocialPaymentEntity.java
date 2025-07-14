package com.spark.Entity;

import com.spark.dto.SocialPaymentDTO;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class SocialPaymentEntity {
	private int payment_id;//결제번호
	private int attendance_id;//수강정보pk
	private String payment_type;//결제종류
	private boolean is_paid;//결제 유무
	
	public SocialPaymentEntity(SocialPaymentDTO dto) {
		this.payment_id = dto.getPayment_id();
		this.attendance_id = dto.getAttendance_id();
		this.payment_type = dto.getPayment_type();
		this.is_paid = dto.is_paid();
	}
}
