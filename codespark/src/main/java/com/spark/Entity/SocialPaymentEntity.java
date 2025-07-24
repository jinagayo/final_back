package com.spark.Entity;

import com.spark.dto.SocialPaymentDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class SocialPaymentEntity {
	@Id
	@Column(name = "payment_id")
	private int paymentId;//결제번호
	
	@Column(name = "class_id")
	private int classId;//수강정보pk
	
	@Column(name = "payment_type")
	private String paymentType;//결제종류
	
	@Column(name = "payment_code")
	private String payment_code;//결제 코드
	@Column(name = "")
	private boolean isPaid;//결제 유무
	
	
	
	

	public SocialPaymentEntity(SocialPaymentDTO dto) {
		this.paymentId = dto.getPayment_id();
		this.classId = dto.getClass_id();
		this.paymentType = dto.getPayment_type();
		this.isPaid = dto.is_paid();
	}
}
