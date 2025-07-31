package com.spark.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.spark.Entity.UserEntity;
import com.spark.dto.ProfileUpdateDTO;
import com.spark.dto.SocialPaymentDTO;
import com.spark.dto.StudentDTO;
import com.spark.dto.TeacherDTO;
import com.spark.dto.UserDTO;
import com.spark.Entity.StudentEntity;
import com.spark.Entity.TeacherEntity;
import com.spark.service.CourseService;
import com.spark.service.S3Service;
import com.spark.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

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
	
	@Autowired
	private S3Service s3Service;
	
    @GetMapping("Profile")
	public ResponseEntity<UserEntity> profile(HttpSession session){
    	System.out.println("profile 컨트롤러 작동중");
    	String id = (String)session.getAttribute("login");
    	UserEntity data = userService.UserProfile(id);
    	System.out.println("data:" + data);
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
    @PostMapping("ProfileUpdate")
    public ResponseEntity<?> updateProfile(HttpSession session,
    		@RequestBody ProfileUpdateDTO dto
    	) {
    	System.out.println("ProfileUpdate 컨트롤러 작동중");
    	String id = (String)session.getAttribute("login");
    	dto.setUser_id(id);
    	UserEntity entity = userService.UserProfile(id);
    	System.out.println(dto);

        if (dto.getPw() != null) entity.setPw(dto.getPw());
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getAddress1() != null) entity.setAddress1(dto.getAddress1());
        if (dto.getAddress2() != null) entity.setAddress2(dto.getAddress2());
        if (dto.getAddressnum() != null) entity.setAddressnum(dto.getAddressnum());
        if (dto.getBirthday() != null) {
            // dto.getBirthday()가 Date 타입일 때
            entity.setBirthday(dto.getBirthday().toString()); // 또는 원하는 포맷으로 변환
        }
        if (dto.getPhone() != null) entity.setPhone(dto.getPhone());
        if (dto.getEmail() != null) entity.setEmail(dto.getEmail());
        if (dto.getImg() != null) entity.setImg(dto.getImg());
        if (dto.getPosition() != null) entity.setPosition(dto.getPosition());
        if (dto.getState() != null) entity.setState(dto.getState());
    	
    	UserEntity data = userService.UserUpdate(entity);
    	return ResponseEntity.ok(data);
    	
    }
    
    @PostMapping("ProfileImageUpload")
    public ResponseEntity<Map<String, Object>> uploadProfileImage(
    		@RequestParam("file") MultipartFile file,
    		@RequestParam(value = "folderName", defaultValue = "user/profile") String folderName
    		){
    	try {
    		String key = s3Service.upload(file, folderName);
    		Map<String, Object> result = new HashMap<>();
    		result.put("key", key);
    		return ResponseEntity.ok(result);
    	}catch(Exception e) {
    		e.printStackTrace();
    		Map<String, Object> error = new HashMap<>();
    		error.put("message", "이미지 업로드 실패: " + e.getMessage());
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    	}
    }
}
