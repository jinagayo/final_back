package com.spark.dto;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
//사용자
public class UserDTO {
	private String user_id;//아이디
	private String pw;//비밀번호
	private String name;//이름
	private String address1;//기본주소
	private String address2;//상세주소
	private String addrssnum;//우편번호
	private Date birthday;//생년월일
	private String phone;//전화번호
	private String position;//권한
	private String email;//이메일
	private String img;//사진
}
