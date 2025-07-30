package com.spark.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spark.Entity.UserEntity;
import com.spark.dto.SocialPaymentDTO;
import com.spark.dto.StudentDTO;
import com.spark.dto.TeacherDTO;
import com.spark.Entity.StudentEntity;
import com.spark.Entity.TeacherEntity;
import com.spark.service.CourseService;
import com.spark.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/Mypage/")
@CrossOrigin(origins="http://localhost:3000", allowCredentials="true") // CORS 설정 추가
public class MyPageController {

	@Autowired
	private PasswordEncoder pwEncoder;
	
	@Autowired
	private UserService userService;
	@Autowired
	private CourseService courseService;
	
    @GetMapping("Profile")
	public ResponseEntity<UserEntity> profile(HttpSession session){
    	System.out.println("profile 컨트롤러 작동중");
    	String id = (String)session.getAttribute("login");
    	UserEntity data = userService.UserProfile(id);
    	System.out.println(data);
        return ResponseEntity.ok().body(data);
	}    
    @GetMapping("Student")
	public ResponseEntity<StudentEntity> student(HttpSession session){
    	System.out.println("student 컨트롤러 작동중");
    	String id = (String)session.getAttribute("login");
    	StudentEntity data = userService.Student(id);
    	System.out.println("스튜던트"+data);

        return ResponseEntity.ok().body(data);
       
	}
    
    @GetMapping("Teacher")
	public ResponseEntity<?> teacher(HttpSession session){
    	System.out.println("teacher 컨트롤러 작동중");
    	String id = (String)session.getAttribute("login");
    	TeacherEntity teacher = userService.Teacher(id);
    	System.out.println("선생정보"+teacher);
    	List<Map<String,Object>> list = courseService.getCategories("SUB");
    	for(Map<String,Object> l : list)System.out.println(l);
    	Map<String,Object> data = new HashMap<>();
    	data.put("teacher", teacher);
    	data.put("list", list);
    	
    	
        return ResponseEntity.ok().body(data);
	}
    @PostMapping("StudentUpdate")
    public ResponseEntity<?> StudentUpdate(HttpSession session,@RequestBody  StudentDTO dto) {
    	System.out.println("StudentUpdate 컨트롤러 작동중");
    	String id = (String)session.getAttribute("login");
    	dto.setStud_id(id);
    	System.out.println(dto);
    	StudentEntity  updated=userService.StudentUpdate(dto);
    	
        return ResponseEntity.ok(updated);
    	
    }
    @PostMapping("TeacherUpdate")
    public ResponseEntity<?> TeacherUpdate(HttpSession session,@RequestBody  TeacherDTO dto) {
    	System.out.println("TeacherUpdate 컨트롤러 작동중");
    	String id = (String)session.getAttribute("login");
    	dto.setTeachId(id);
    	System.out.println(dto);
    	TeacherEntity  updated=userService.TeacherUpdate(dto);
    	
        return ResponseEntity.ok(updated);
    	
    }
    @PostMapping("passwordCheck")
    public ResponseEntity<?> passwordCheck(HttpSession session,@RequestBody String pw) {
    	System.out.println("passwordCheck 컨트롤러 작동중");
    	String id = (String)session.getAttribute("login");
    	UserEntity user = userService.UserProfile(id);
    	System.out.println(pw);

		//비밀번호 확인
		if(!pwEncoder.matches(pw, user.getPw())) {
            Map<String, Object> response = new HashMap<>();
            return ResponseEntity.badRequest().body(response);
		}
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "비밀번호가 맞습니다.");
		return ResponseEntity.ok(response);
    		
    }
    	
}
