package com.spark.service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.spark.Entity.UserEntity;
import com.spark.dto.UserDTO;
import com.spark.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder pwencoder;

    // 학생 회원가입
    public ResponseEntity<?> createStudent(UserDTO request) {
        try {
            // 필수 입력 검증
            if (isNullOrEmpty(request.getUser_id())) return error("아이디는 필수입니다.");
            if (isNullOrEmpty(request.getPw())) return error("비밀번호는 필수입니다.");
            if (request.getPw().length() < 8) return error("비밀번호는 8자 이상이어야 합니다.");
            if (request.getConfirmPassword() != null && !request.getPw().equals(request.getConfirmPassword()))
                return error("비밀번호가 일치하지 않습니다.");
            if (isNullOrEmpty(request.getName())) return error("이름은 필수입니다.");
            if (isNullOrEmpty(request.getEmail())) return error("이메일은 필수입니다.");

            // 중복 체크
            if (userRepo.existsByUserId(request.getUser_id())) return error("이미 존재하는 아이디입니다.");
            if (userRepo.existsByEmail(request.getEmail())) return error("이미 존재하는 이메일입니다.");

            // 비밀번호 암호화
            String encodedPassword = pwencoder.encode(request.getPw());

            // Entity 생성
            UserEntity student = new UserEntity();
            student.setUserId(request.getUser_id());
            student.setPw(encodedPassword);
            student.setName(request.getName());
            student.setAddress1(request.getAddress1());
            student.setAddress2(request.getAddress2());
            student.setAddressnum(request.getAddressnum());
            student.setBirthday(request.getBirthday());
            student.setPhone(request.getPhone());
            student.setEmail(request.getEmail());
            student.setPosition("1"); // 학생

            // 필수값 사전 체크 (position 등)
            if (isNullOrEmpty(student.getPosition())) return error("권한값 누락(position)");
            if (isNullOrEmpty(student.getUserId()) || isNullOrEmpty(student.getPw()) ||
                isNullOrEmpty(student.getName()) || isNullOrEmpty(student.getEmail()))
                return error("필수값 누락");

            // 저장
            UserEntity savedStudent = userRepo.save(student);

            // 성공 응답
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "수강생 회원가입이 완료되었습니다!");
            response.put("userId", savedStudent.getUserId());
            response.put("position", savedStudent.getPosition());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace(); // **전체 스택 출력**
            return serverError("회원가입 처리 중 오류가 발생했습니다.", e);
        }
    }

    // 강사 회원가입
    public ResponseEntity<?> createInstructor(UserDTO request) {
        try {
            if (isNullOrEmpty(request.getUser_id())) return error("아이디는 필수입니다.");
            if (isNullOrEmpty(request.getPw())) return error("비밀번호는 필수입니다.");
            if (request.getPw().length() < 8) return error("비밀번호는 8자 이상이어야 합니다.");
            if (request.getConfirmPassword() != null && !request.getPw().equals(request.getConfirmPassword()))
                return error("비밀번호가 일치하지 않습니다.");
            if (isNullOrEmpty(request.getName())) return error("이름은 필수입니다.");
            if (isNullOrEmpty(request.getEmail())) return error("이메일은 필수입니다.");

            if (userRepo.existsByUserId(request.getUser_id())) return error("이미 존재하는 아이디입니다.");
            if (userRepo.existsByEmail(request.getEmail())) return error("이미 존재하는 이메일입니다.");

            String encodedPassword = pwencoder.encode(request.getPw());

            UserEntity teacher = new UserEntity();
            teacher.setUserId(request.getUser_id());
            teacher.setPw(encodedPassword);
            teacher.setName(request.getName());
            teacher.setAddress1(request.getAddress1());
            teacher.setAddress2(request.getAddress2());
            teacher.setAddressnum(request.getAddressnum());
            teacher.setBirthday(request.getBirthday());
            teacher.setPhone(request.getPhone());
            teacher.setEmail(request.getEmail());
            teacher.setPosition("2"); // 강사

            if (isNullOrEmpty(teacher.getPosition())) return error("권한값 누락(position)");
            if (isNullOrEmpty(teacher.getUserId()) || isNullOrEmpty(teacher.getPw()) ||
                isNullOrEmpty(teacher.getName()) || isNullOrEmpty(teacher.getEmail()))
                return error("필수값 누락");

            UserEntity savedTeacher = userRepo.save(teacher);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "강사 회원가입이 완료되었습니다!");
            response.put("userId", savedTeacher.getUserId());
            response.put("position", savedTeacher.getPosition());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return serverError("회원가입 처리 중 오류가 발생했습니다.", e);
        }
    }

    // 아이디 사용 가능 여부 확인
    public ResponseEntity<?> checkUserIdAvailability(String userId) {
        try {
            boolean available = !userRepo.existsByUserId(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("available", available);
            response.put("message", available ? "사용 가능한 아이디입니다." : "이미 사용 중인 아이디입니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return serverError("아이디 확인 중 오류가 발생했습니다.", e);
        }
    }

    // 이메일 사용 가능 여부 확인
    public ResponseEntity<?> checkEmailAvailability(String email) {
        try {
            boolean available = !userRepo.existsByEmail(email);
            Map<String, Object> response = new HashMap<>();
            response.put("available", available);
            response.put("message", available ? "사용 가능한 이메일입니다." : "이미 사용 중인 이메일입니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return serverError("이메일 확인 중 오류가 발생했습니다.", e);
        }
    }

    // 공통 오류 응답
    private ResponseEntity<Map<String, Object>> error(String msg) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", msg);
        return ResponseEntity.badRequest().body(response);
    }

    // 서버 오류 응답
    private ResponseEntity<Map<String, Object>> serverError(String msg, Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", msg + (e.getMessage() != null ? " [" + e.getMessage() + "]" : ""));
        return ResponseEntity.internalServerError().body(response);
    }

    // 문자열 null/empty 체크 유틸
    private boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
