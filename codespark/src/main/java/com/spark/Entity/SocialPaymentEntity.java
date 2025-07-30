package com.spark.Entity;

import com.spark.dto.SocialPaymentDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int paymentId;//결제번호
	
	@Column(name = "class_id")
	private String classId;//수강정보pk
	
	@Column(name = "payment_type")
	private String paymentType;//결제종류
	
	@Column(name = "payment_code")
	private String paymentCode;//결제 코드
	
	@Column(name = "is_paid")
	private boolean isPaid;//결제 유무
	
	@Column(name="price")
	private int price;

	@Column(name = "user_id")
	private String user_id;
	


	

	public SocialPaymentEntity(SocialPaymentDTO dto) {
		this.paymentId = dto.getPayment_id();
		this.classId = dto.getClass_id();
		this.paymentType = dto.getPayment_type();
		this.isPaid = dto.is_paid();
		this.price = dto.getPrice();
		this.paymentCode=dto.getPayment_code();
		this.user_id=dto.getUser_id();
	}
}
