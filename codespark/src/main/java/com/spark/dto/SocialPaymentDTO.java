package com.spark.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
//소셜결제
public class SocialPaymentDTO {
	private int payment_id;//결제번호
	private int attendance_id;//수강정보pk
	private String payment_type;//결제종류
	private boolean is_paid;//결제 유무
}
