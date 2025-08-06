package com.spark.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.spark.repository.StudentRepository;
import com.spark.repository.TeacherRepository;
import com.spark.repository.UserRepository;
import com.spark.Entity.StudentEntity;
import com.spark.Entity.TeacherEntity;
import com.spark.Entity.UserEntity;
import com.spark.dto.StudentDTO;
import com.spark.dto.TeacherDTO;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TeacherRepository teacherRepository;
	@Autowired
	private PasswordEncoder pwEncoder;

    public List<UserEntity> getUsersByPosition(String position) {
        return userRepository.findByPosition(position);
    }

    //페이징
    public Page<UserEntity> getStudentsPaginated(Pageable pageable, String search) {
        if (search == null || search.trim().isEmpty()) {
            // 검색어가 없으면 position이 "1"인 모든 학생 조회
            return userRepository.findByPosition("1", pageable);
        } else {
            // 검색어가 있으면 검색 조건 추가
            return userRepository.findStudentsWithSearch("1", search.trim(), pageable);
        }
    }
    
    public Page<UserEntity> getTeachersPaginated(Pageable pageable, String search) {
        if (search == null || search.trim().isEmpty()) {
            return userRepository.findByPosition("2", pageable);
        } else {
            return userRepository.findStudentsWithSearch("2", search.trim(), pageable);
        }
    }
    
    //승인대기중인 강사 
    public Page<UserEntity> getZeroPositionUsers(Pageable pageable, String search) {
        if (search == null || search.trim().isEmpty()) {
            return userRepository.findByPosition("0", pageable);
        } else {
            return userRepository.findByPositionZeroWithSearch(search.trim(), pageable);
        }
    }

    public UserEntity UserProfile(String id) {
        return userRepository.findById(id)
                 .orElse(null); 
    }

	public StudentEntity Student(String id) {
		return studentRepository.findById(id)
                .orElse(null); 
	}

	public StudentEntity StudentUpdate(StudentDTO dto) {
		StudentEntity  entity = new StudentEntity(dto);
		return studentRepository.save(entity);
	}

	public TeacherEntity Teacher(String id) {
		return teacherRepository.findById(id)
                .orElse(null); 
	}

	public TeacherEntity TeacherUpdate(TeacherDTO dto) {
		TeacherEntity  entity = new TeacherEntity(dto);
		return teacherRepository.save(entity);
	
	}

	public UserEntity UserUpdate(UserEntity entity) {
		return userRepository.save(entity);
	
	}

	//비밀번호확인
	public boolean passWordCheck(String id, String inputPassword) {
    	UserEntity user =UserProfile(id);
    	if(pwEncoder.matches(inputPassword, user.getPw())) return true;
    	else return false;
	}

	//회원 삭제
	public void deleteUser(String id) {
    	UserEntity user =UserProfile(id);
    	System.out.println(user);
    	if(user.getPosition().equals("1")||Integer.parseInt(user.getPosition())==1) {
    		deleteStudent(id);
    	}else if(user.getPosition().equals("2")||Integer.parseInt(user.getPosition())==2) {
    		deleteTeacher(id);
    	}
    	
    	userRepository.delete(user);
		
	}
	public void deleteStudent(String id) {
		StudentEntity student = Student(id);
		System.out.println(student);
		if(student !=null)studentRepository.delete(student);
	}
	public void deleteTeacher(String id) {
		TeacherEntity teacher = Teacher(id);
		if(teacher !=null)teacherRepository.delete(teacher);
	}

	public void pwChange(String id, String pw) {
		UserEntity user = UserProfile(id);
		String encodedPassword = pwEncoder.encode(pw);
		user.setPw(encodedPassword);
		userRepository.save(user);
		
	}
}
