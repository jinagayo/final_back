package com.spark.controller;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spark.Entity.MeterialEntity;
import com.spark.Entity.UserEntity;
import com.spark.dto.VideoUploadRequest;
import com.spark.repository.MeterialRepository;
import com.spark.repository.UserRepository;
import com.spark.repository.VideoRepository;
import com.spark.service.S3Service;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/video")
@CrossOrigin(origins="http://localhost:3000", allowCredentials="true")
public class VideoController {
	private final S3Service s3Service;
	private final MeterialRepository meterialRepository;
	
	// 강사(2)만 업로드 가능
	@PostMapping("/upload")
	public ResponseEntity<?> saveVideo(@RequestParam String filename, Principal principal) {
	    // 🔍 인증 확인 로그
	    System.out.println("📍 Principal: " + (principal != null ? principal.getName() : "null"));

	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    System.out.println("📍 인증 객체: " + auth);
	    System.out.println("📍 권한 목록: " + auth.getAuthorities());

	    // 👉 여기서 userId로 유저 정보 조회해서 권한 체크도 가능
	    // ex) userRepository.findByUserId(principal.getName())...

	    String key = "videos/" + UUID.randomUUID() + "_" + filename;
	    String uploadUrl = s3Service.generatePresignedUploadUrl(key);
	    return ResponseEntity.ok(Map.of("uploadUrl", uploadUrl, "key", key));
	}

	@PostMapping("/save")
	public ResponseEntity<?> saveVideoMeterial(@RequestBody VideoUploadRequest request){
		MeterialEntity video = new MeterialEntity();
		 int nextSeq = meterialRepository.findNextSeqByClassId(request.getClassId()) + 1;

		video.setTitle(request.getTitle());
		video.setContent(request.getKey());
		video.setClassId(request.getClassId());
		video.setDetail(request.getDetail());
		video.setType("MET001");
		video.setSeq(nextSeq);
		
		meterialRepository.save(video);
		return ResponseEntity.ok("저장완료");
	}
}