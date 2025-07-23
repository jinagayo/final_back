package com.spark.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.spark.repository.UserRepository;
import com.spark.Entity.UserEntity;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserEntity> getUsersByPosition(String position) {
        return userRepository.findByPosition(position);
    }

    //페이징
    public Page<UserEntity> getStudentsPaginated(Pageable pageable, String search) {
        if (search == null || search.trim().isEmpty()) {
            return userRepository.findByPosition("1", pageable);
        } else {
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

}
