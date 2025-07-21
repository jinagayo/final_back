package com.spark.Entity;

import com.spark.dto.SocialPaymentDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "social_payment")
@NoArgsConstructor
public class SocialPaymentEntity {
	@Id
	@Column(name = "payment_id")
	private int paymentId;//결제번호
	
	@Column(name = "attendance_id")
	private int attendanceId;//수강정보pk
	
	@Column(name = "payment_type")
	private String paymentType;//결제종류
	
	@Column(name = "is_paid")
	private boolean isPaid;//결제 유무
	
	public SocialPaymentEntity(SocialPaymentDTO dto) {
		this.paymentId = dto.getPayment_id();
		this.attendanceId = dto.getAttendance_id();
		this.paymentType = dto.getPayment_type();
		this.isPaid = dto.is_paid();
	}
}
