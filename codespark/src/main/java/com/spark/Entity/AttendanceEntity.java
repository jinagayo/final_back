package com.spark.Entity;

import com.spark.dto.AttendanceDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "attendance")
@Data
@NoArgsConstructor
public class AttendanceEntity {
    
    @Id
    @Column(name = "att_id") // DB 컬럼명 명시
    private int attId;
    
    @Column(name = "class_id")
    private String classId; 
    
    @Column(name = "stu_id")
    private String stuId; 
    
    @Column(name = "price")
    private int price;
    
    @Column(name = "state")
    private String state;
    
    @Column(name = "reviewnum")
    private int reviewnum;
    
    @Column(name = "payment_id")
    private int paymentId; // 카멜케이스로 변경
    
    public AttendanceEntity(AttendanceDTO dto) {
        this.attId = dto.getAtt_id();
        this.classId = dto.getClass_id();
        this.stuId = dto.getStu_id();
        this.price = dto.getPrice();
        this.state = dto.getState();
        this.reviewnum = dto.getReviewnum();
        this.paymentId = dto.getPayment_id();
    }
}