package com.spark.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spark.repository.UserRepository;
import com.spark.service.CourseService;
import com.spark.service.JoinService;

@RestController
@RequestMapping("/course/")
@CrossOrigin(origins="http://localhost:3000", allowCredentials="true")
public class CourseController {

    @Autowired
    private CourseService courseService;
    
    @GetMapping("List")
    public ResponseEntity<?> getAllCourses() {
    	System.out.println("컨트롤러 작동중");
        return courseService.getAllCourses();
    }
}
