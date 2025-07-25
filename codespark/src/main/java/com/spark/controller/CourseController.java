package com.spark.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.spark.dto.AttendanceDTO;
import com.spark.dto.ClassDTO;
import com.spark.dto.ClassInfoDTO;
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
        //payment update에 필요한 데이터
        payment.set_paid(true);
        System.out.println(payment);
    	courseService.savePaymentInfo(payment);
    	//payment의 pk 가져오기
    	Integer paymentPK = courseService.getPaymentPK(payment.getPayment_code());
    	//attendance update에 필요한 데이터
    	AttendanceDTO attDto = new  AttendanceDTO();
    	attDto.setPaymentId(paymentPK);
    	attDto.setStuId(payment.getUser_id());;
    	attDto.setClassId(payment.getClass_id());
    	attDto.setPrice(payment.getPrice());
    	attDto.setState("ATT001");
        System.out.println(attDto);
    	courseService.saveAttendInfo(attDto);
    	
        return ResponseEntity.ok("결제 및 수강정보 저장 완료");
    }
    
    @GetMapping("PaymentEnd")
	public ResponseEntity<Boolean> PaymentEnd(@RequestParam String class_id,HttpSession session){
    	System.out.println("PaymentEnd 컨트롤러 작동중");
    	
    	return null;
	}
    
    @GetMapping("teacher/Application")
    public ResponseEntity<?> TeacherApplication() {
    	System.out.println("TeacherApplication 작동중");
    	List<Map<String,Object>>  data =courseService.getCategories("SUB");
        return ResponseEntity.ok().body(data);
    }
    
    @PostMapping("teacher/formsubmit")
    public ResponseEntity<?> formsubmit(@ModelAttribute  ClassDTO submit,HttpSession session) {
        String id = (String)session.getAttribute("login");
    	System.out.println("formsubmit 작동중");
    	submit.setTeachId(id);
    	submit.setCreatedBy(id);
    	System.out.println("SUBMIT:"+submit);
    	courseService.teacherApplication(submit);
    	
        return ResponseEntity.ok("강의 신청 완료");
  
    	
    }
    
    @GetMapping("teacher/List")
    public ResponseEntity<?> TeacherList(HttpSession session) {
    	System.out.println("TeacherList 작동중");
        String id = (String)session.getAttribute("login");
    	//과목 카테고리
    	List<Map<String,Object>> rowcat =courseService.getCategories("SUB");
    	List<String> cat = new ArrayList<>();
    	for(Map<String,Object> r : rowcat) {
    		cat.add((String)r.get("name"));
    	}
    	//강의 리스트
    	List<ClassInfoDTO> classList = courseService.getClassList(id);
    	for(ClassInfoDTO d : classList) System.out.println(d);
    	Map<String,Object> data = new HashMap<>();
    	data.put("categories", cat);
    	data.put("applications", classList);
    	
        return ResponseEntity.ok().body(data);
    }
}