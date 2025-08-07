package com.spark.Entity;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.spark.dto.AttendanceDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "attendance")
@Data

@SQLDelete(sql = "UPDATE attendance SET is_active = false WHERE att_id = ?")
@Where(clause = "is_active = true")
@NoArgsConstructor
public class AttendanceEntity {
    @Id
    @Column(name = "att_id") // DB 컬럼명 명시
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private int attId;
    
    @Column(name = "class_id")
    private String classId; 
    
    @Column(name = "stu_id")
    private String stuId; 
    
    @Column(name = "price")
    private int price;
    
    @Column(name = "state")
    private String state;

    
    @Column(name = "payment_id")
    private Integer paymentId; // 카멜케이스로 변경
    
    public AttendanceEntity(AttendanceDTO dto) {
        this.attId = dto.getAttId();
        this.classId = dto.getClassId();
        this.stuId = dto.getStuId();
        this.price = dto.getPrice();
        this.state = dto.getState();
        this.paymentId = dto.getPaymentId();
    }
}
