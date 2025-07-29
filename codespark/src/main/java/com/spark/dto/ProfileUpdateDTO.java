package com.spark.dto;

import java.util.Date;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ProfileUpdateDTO {
    
    private String user_id;
    
   private String pw;
    
    private String name;
    
    private String address1;
    
    private String address2;
    
    private String addressnum;
    
    private Date birthday;
    
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$|^[0-9-]+$", message = "전화번호 형식이 올바르지 않습니다.")
    private String phone;
    
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;
    
    private String img;
    
    private String position;
    
    private String state;
    
}