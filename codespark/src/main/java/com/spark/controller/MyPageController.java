package com.spark.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spark.Entity.UserEntity;
<<<<<<< Updated upstream
=======
import com.spark.controller.BoardController.ApiResponse;
import com.spark.dto.BoardDTO;
import com.spark.dto.ProfileUpdateDTO;
>>>>>>> Stashed changes
import com.spark.dto.SocialPaymentDTO;
import com.spark.dto.StudentDTO;
import com.spark.dto.TeacherDTO;
import com.spark.Entity.StudentEntity;
import com.spark.Entity.TeacherEntity;
import com.spark.service.BoardService;
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
	
<<<<<<< Updated upstream
=======
	@Autowired
	private S3Service s3Service;
	
	@Autowired
	private BoardService boardService;
	
>>>>>>> Stashed changes
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
    	
<<<<<<< Updated upstream
=======
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

    /*
    @GetMapping("subject/{subId}/classes")
    public ResponseEntity<ApiResponse> getClassesBySubject(@PathVariable("subId") String subId) {
        try {
            List<Map<String, Object>> classes = courseService.getClassesBySubjectId(subId);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("classes", classes);
            responseData.put("subId", subId);
            
            return ResponseEntity.ok(new ApiResponse(true, "과목별 강의 조회 성성", responseData, 1));
            
        } catch (Exception e) {
            System.err.println("과목별 강의 조회 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            	    .body(new ApiResponse(false, "조회 실패: " + e.getMessage(), null, 0));
        }
    
    }
	*/
    
>>>>>>> Stashed changes
}
