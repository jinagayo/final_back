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
    public ResponseEntity<?> updatePayment(@RequestBody SocialPaymentDTO payment) {
    	System.out.println("PaymentUpdate 컨트롤러 작동중");
        // paymentService.save(payment);
        return ResponseEntity.ok("결제 정보 저장 완료");
    }
    
    @GetMapping("PaymentEnd")
	public ResponseEntity<Boolean> PaymentEnd(@RequestParam String class_id,HttpSession session){
        String login_id = (String)session.getAttribute("login");
    	System.out.println("PaymentEnd 컨트롤러 작동중");
    	
    	return courseService.PaymentEnd(class_id,login_id);
	}
}