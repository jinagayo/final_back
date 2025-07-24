package com.spark.Entity;
import com.spark.dto.AttendanceDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "attendance")
@Data
@NoArgsConstructor
public class AttendanceEntity {
    @Id
    @Column(name = "att_id") // DB 컬럼명 명시
    private String attId;
    
    @Column(name = "class_id")
    private String classId; 
    
    @Column(name = "stu_id")
    private String stuId; 
    
    @Column(name = "price")
    private int price;
    
    @Column(name = "state")
    private String state;

    
    @Column(name = "payment_id")
    private String paymentId; // 카멜케이스로 변경
    
    public AttendanceEntity(AttendanceDTO dto) {
        this.attId = dto.getAtt_id();
        this.classId = dto.getClass_id();
        this.stuId = dto.getStu_id();
        this.price = dto.getPrice();
        this.state = dto.getState();
        this.paymentId = dto.getPayment_id();
    }
}
