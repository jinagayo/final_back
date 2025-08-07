package com.spark.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins="http://localhost:3000", allowCredentials="true") // CORS 설정 추가
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "/"; // index.html 또는 index.jsp 등 뷰 이름
    }
}
