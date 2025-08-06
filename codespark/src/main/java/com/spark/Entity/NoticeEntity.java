package com.spark.Entity;

import java.sql.Timestamp;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.spark.dto.ClassDTO;

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
@Table(name = "notice")
@SQLDelete(sql = "UPDATE notice SET is_active = false WHERE notice_id = ?")
@Where(clause = "is_active = true")
@NoArgsConstructor
public class NoticeEntity {
	@Id
	@Column(name = "notice_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int noticeId;//알림코드

	@Column(name = "user_id")
    private String userId; 


	@Column(name = "type")
    private String type; //NOT001:공지사항, NOT002:강의, NOT00n:행정업무

	@Column(name = "target")
    private String target;  //공지사항 or 강의 아이디

	@Column(name = "state")
    private boolean state = false; //읽음 여부
	
	@Column(name = "is_active")
    private boolean isActive = true;

	
	@Column(name = "created_at", insertable = false, updatable = false)
	private Timestamp createdAt;

	@Column(name = "updated_at", insertable = false, updatable = false)
	private Timestamp updatedAt;

	
	@Column(name="updated_by")
	private String updatedBy;
	
	@Column(name="created_by")
	private String createdBy;

	
//	public NoticeEntity() {
//	}
}