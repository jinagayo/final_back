package com.spark.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.spark.repository.BoardRepository;
import com.spark.repository.CodingRepository;
import com.spark.repository.CourseRepository;
import com.spark.repository.UserRepository;

@Service
public class SearchService {
	@Autowired
	private CourseRepository courseRepo;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private BoardRepository boardRepo;
	
	@Autowired
	private CodingRepository codingRepo;
	
    public Map<String, Object> searchData(String keyword, int page, int limit) {
        System.out.println("=== SearchService 진입 ===");
        System.out.println("검색어: " + keyword);
        
        List<Map<String, Object>> results = new ArrayList<>();
        
        try {
            // 강의 검색
            List<Map<String, Object>> classResults = courseRepo.searchClasses(keyword);
            System.out.println("강의 검색 결과: " + classResults.size() + "개");
            results.addAll(classResults);
            
            // 게시글 검색
            List<Map<String, Object>> boardResults = boardRepo.searchBoards(keyword)
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
            System.out.println("게시글 검색 결과: " + boardResults.size() + "개");
            results.addAll(boardResults);
            
            //코딩문제 검색
            List<Map<String, Object>> codingResult = codingRepo.searchCoding(keyword);
            System.out.println("코딩문제 검색 결과: " + boardResults.size() + "개");
            results.addAll(codingResult);
            
            
            System.out.println("전체 검색 결과: " + results.size() + "개");
            
            return Map.of(
                "success", true,
                "results", results,
                "total", results.size(),
                "query", keyword
            );
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of(
                "success", false,
                "message", "검색 실패: " + e.getMessage(),
                "results", new ArrayList<>(),
                "total", 0
            );
        }
    }
}
