package com.spark.Entity;

import java.util.Date;

import com.spark.dto.UserDTO;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class UserEntity {
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
	
	public UserEntity(UserDTO dto) {
		this.user_id = dto.getUser_id();
		this.pw = dto.getPw();
		this.name = dto.getName();
		this.address1 = dto.getAddress1();
		this.address2 = dto.getAddress2();
		this.birthday = dto.getBirthday();
		this.phone = dto.getPhone();
		this.position = dto.getPosition();
		this.email = dto.getEmail();
		this.img = dto.getImg();
	}
}
