package com.spark.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spark.service.ClassService;
import com.spark.service.CourseService;
import com.spark.Entity.MeterialEntity;
import com.spark.Entity.MeterialSubEntity;
import com.spark.Entity.TestEntity;
import com.spark.Entity.TestSubEntity;
import com.spark.dto.ClassInfoDTO;
import com.spark.dto.MeterialDTO;
import com.spark.dto.SubjectReviewDTO;
import com.spark.dto.TestDTO;
import com.spark.dto.TestSubDTO;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/myclass/")
@CrossOrigin(origins="http://localhost:3000", allowCredentials="true")
public class ClassController {
    @Autowired
    private ClassService classService;
    
    @GetMapping("List")
    public ResponseEntity<?> getAllClass(HttpSession session) {
    	System.out.println("getAllClass 작동중");
        String id = (String)session.getAttribute("login");
    	System.out.println(id);
        List<Map<String, Object>> data = classService.getAllClass(id);
        return ResponseEntity.ok().body(data);
    }    
    
    @GetMapping("Main")
    public ResponseEntity<?> Main(HttpSession session,@RequestParam("class_id") String classId) {
    	System.out.println("Main 작동중");
        String id = (String)session.getAttribute("login");
        //수업 정보
        ClassInfoDTO classDto = classService.getClass(classId);
        //수업자료
        List<MeterialEntity> meterial = classService.getMeterials(Integer.parseInt(classId));
        for(MeterialEntity m : meterial)System.out.println(m);

        //리뷰
        Integer attId = classService.getAttId(id,classId);
        Boolean review = classService.reviewYN(attId);
        System.out.println(review);
        
        Map<String, Object> map = new HashMap<>();
        map.put("class", classDto);
        map.put("material", meterial);
        map.put("review", review);
        
        
    	
        return ResponseEntity.ok().body(map);
    }
    
    @PostMapping("review")
    public ResponseEntity<?> review(HttpSession session,@RequestBody SubjectReviewDTO dto) {
    	System.out.println("review 작동중");
        String id = (String)session.getAttribute("login");
        dto.setAttId(classService.getAttId(id,dto.getClass_id()));
    	System.out.println(dto);
    	classService.saveReview(dto);
    	
    	
        return ResponseEntity.ok().body("");
    }

    @GetMapping("assignment")
    public ResponseEntity<?> assignment(@RequestParam("meterial_id") String meterialId) {
    	System.out.println("assignment 작동중");
    	MeterialEntity data = classService.getMeterialOne(meterialId);
    	System.out.println(data);
    	return  ResponseEntity.ok().body(data);
    }

    @GetMapping("test")
    public ResponseEntity<?> test(HttpSession session,@RequestParam("meterial_id") String meterialId) {
    	System.out.println("test 작동중");
        String id = (String)session.getAttribute("login");
    	MeterialEntity meterial = classService.getMeterialOne(meterialId);
    	List<TestEntity> test = classService.getTest(meterialId);
    	Map<String,Object> data = new HashMap<>();
    	data.put("title", meterial.getTitle());
    	data.put("content", meterial.getContent());
    	data.put("time", meterial.getTime());
    	data.put("questions", test);
    	Boolean bool = classService.testYN(meterialId,id);
    	data.put("submit", bool);
    	System.out.println(data);
    	return  ResponseEntity.ok().body(data);
    }

    @PostMapping("testsubmit")
    public ResponseEntity<?> testsubmit(@RequestParam("meterial_id") String meterialId,
    		HttpSession session, @RequestBody List<TestSubDTO> list) {
    	System.out.println("testsubmit 작동중");
        String id = (String)session.getAttribute("login");
        double total =0 , correct=0;
        for(TestSubDTO dto : list) {
        	classService.testSubmit(id,dto);
        	System.out.println("dto"+dto);
        	total++;
        	if(dto.isCorrect()) correct++;
        	
        }
    	classService.materialTestDone(meterialId,id,100*correct/total);
    	
        return ResponseEntity.ok("");
    }

    @GetMapping("testresult")
    public ResponseEntity<?> testresult(HttpSession session,@RequestParam("meterial_id") String meterialId) {
    	System.out.println("testresult 작동중");
        String id = (String)session.getAttribute("login");
    	Map<String,Object> data = new HashMap<>();

    	//테스트정보
    	MeterialEntity metEntity= classService.getMeterialOne(meterialId);
    	data.put("title", metEntity.getTitle());
    	data.put("content", metEntity.getContent());
    	//시험점수
    	MeterialSubEntity subEntity = classService.getMeterialSubOne(meterialId,id);
    	data.put("score", subEntity.getContent());
    	//문제정보
    	List<Map<String,Object>> questions = new ArrayList<>();
    	List<TestEntity> testEntity = classService.getTest(meterialId);
    	for(TestEntity t :testEntity) {
    		Map<String,Object> map = new HashMap<>();
    		map.put("question", t.getQuestion());
    		map.put("answer", t.getAnswer());
    		map.put("choice1", t.getChoice1());
    		map.put("choice2", t.getChoice2());
    		map.put("choice3", t.getChoice3());
    		map.put("choice4", t.getChoice4());
    		TestSubEntity sub = classService.getTestSub(String.format("%d", t.getTestId()),id);
    		map.put("submit", sub.getSubmit());
    		map.put("correct", sub.isCorrect());
    		questions.add(map);
    	}
    	data.put("questions", questions);

        return ResponseEntity.ok().body(data);
    }
    
   
}
