package com.spark.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spark.dto.AttendanceDTO;
import com.spark.dto.SocialPaymentDTO;
import com.spark.service.CourseService;

import jakarta.servlet.http.HttpSession;


@RestController
@RequestMapping("/course/")
@CrossOrigin(origins="http://localhost:3000", allowCredentials="true")
public class CourseController {

    @Autowired
    private CourseService courseService;
    
    
    @GetMapping("List")
    public ResponseEntity<?> getAllCourses() {
    	System.out.println("리스트컨트롤러 작동중");
        return courseService.getAllCourses();
    }
    @GetMapping("Detail")
    public ResponseEntity<?> CoursesDetail(@RequestParam("class_id") String classId) {
    	System.out.println("디테일 컨트롤러 작동중");
    	
        return courseService.getCourses(classId);
    }
    @GetMapping("Payment")
    public ResponseEntity<?> CoursesPay(@RequestParam("class_id") String classId) {
    	System.out.println("페이 컨트롤러 작동중");
    	
        return courseService.getCourses(classId);
    }
    
    @PostMapping("PaymentUpdate")
    public ResponseEntity<?> updatePayment(@RequestBody SocialPaymentDTO payment,HttpSession session) {
    	System.out.println("PaymentUpdate 컨트롤러 작동중");
        String login_id = (String)session.getAttribute("login");
        //payment update에 필요한 데이터
        payment.setUser_id(login_id);
        payment.set_paid(true);
    	courseService.savePaymentInfo(payment);
    	//attendance update에 필요한 데이터
    	AttendanceDTO attDto = null ;
    	attDto.setClass_id(payment.getClass_id());
    	attDto.setPrice(payment.getPrice());
    	attDto.setState("ATT001");
    	attDto.setStu_id(login_id);
    	courseService.saveAttendInfo(attDto);
    	
        return ResponseEntity.ok("결제 및 수강정보 저장 완료");
    }
    
    @GetMapping("PaymentEnd")
	public ResponseEntity<Boolean> PaymentEnd(@RequestParam String class_id){
    	System.out.println("PaymentEnd 컨트롤러 작동중");
    	
    	return null;
	}
}