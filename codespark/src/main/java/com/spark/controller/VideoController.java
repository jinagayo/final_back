package com.spark.controller;

import java.io.File;
import java.net.URL;
import java.security.Principal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
import com.spark.Entity.MeterialSubEntity;
import com.spark.Entity.UserEntity;
import com.spark.dto.MeterialDTO;
import com.spark.dto.StudentDTO;
import com.spark.dto.VideoUploadRequest;
import com.spark.repository.AttendanceRepository;
import com.spark.repository.MeterialRepository;
import com.spark.repository.MeterialSubRepository;
import com.spark.repository.UserRepository;
import com.spark.repository.VideoRepository;
import com.spark.service.MeterialSubService;
import com.spark.service.NoticeService;
import com.spark.service.S3Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/video")
@CrossOrigin(origins="http://localhost:3000", allowCredentials="true")
public class VideoController {
    private final AmazonS3 amazonS3;
	private final S3Service s3Service;
	@Autowired
	private final MeterialRepository meterialRepository;
	@Autowired
	private final AttendanceRepository attRepository;
	@Autowired
	private final MeterialSubRepository materialSubRepository;
	@Autowired
	private final MeterialSubService meterialSubService;
	@Autowired
	private final NoticeService noticeService;
	
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
		 try {
	            MeterialEntity video = new MeterialEntity();
	            video.setClassId(Integer.parseInt(request.getClassId()));
	            video.setTitle(request.getTitle());
	            video.setDetail(request.getDetail());
	            video.setContent(request.getKey());
	            video.setTime(request.getDuration());
	            video.setType("MET001"); // 고정: video
	            int nextSeq = meterialRepository.findNextSeqByClassId(Integer.parseInt(request.getClassId())) + 1;
	            video.setSeq(nextSeq);   // ✅ 자동으로 순번 설정
	            meterialRepository.save(video);
	            
	         // 1. 출석(수강신청) 테이블에서 수강 중인 학생 ID 목록 조회
	            List<String> studentIds = attRepository.findStudentIdsByClassId(video.getClassId());
	            
	         // 2. 진도 테이블에 각 학생마다 초기 진도 0으로 등록
	            List<MeterialSubEntity> progresses = new ArrayList<>();
	            
	            for (String stuId : studentIds) {
	                MeterialSubEntity sub = new MeterialSubEntity();
	                sub.setMeterialId(video.getMeterId());
	                sub.setStdId(stuId);
	                sub.setProgress(0);
	                progresses.add(sub);
	            }
	            
	            materialSubRepository.saveAll(progresses);
	            return ResponseEntity.ok().body(Map.of(
	            	    "message", "✅ 영상 및 진도 저장 완료",
	            	    "meterialId", video.getMeterId()
	            	));

	        } catch (Exception e) {
	            return ResponseEntity
	                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("❌ 저장 실패: " + e.getMessage());
	        }
		 
		 
		 
	}
	
	@GetMapping("/material/{id}")
	public ResponseEntity<?> getMaterialById(@PathVariable int id){
		System.out.println("🔍 요청 받은 meterId: " + id);
		MeterialEntity meterial = meterialRepository.findByMeterId(id);
		System.out.println("🔍 조회 결과 meterial: " + meterial);

		 
		 if (meterial == null) {
		        return ResponseEntity.status(404).body("해당 자료를 찾을 수 없습니다.");
		    }
		 MeterialDTO m = new MeterialDTO(); 
		 m.setClassId(meterial.getClassId());
		 m.setMeterId(meterial.getMeterId());
		 m.setContent(meterial.getContent());
		 m.setDetail(meterial.getDetail());
		 return ResponseEntity.ok(m);
		    
		}
	
	@GetMapping("/stream")
	public ResponseEntity<?> getVideoStreamUrl(@RequestParam String key){
		 final String bucketName = "my-lecture-video"; 
		try {
			//유효시간 1시간 짜리 presigned URL 생성
			Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60);
			GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key)
					.withMethod(HttpMethod.GET)
					.withExpiration(expiration);
			
			URL url = amazonS3.generatePresignedUrl(request);
			
			Map<String, String> response = new HashMap<>();
			response.put("videoUrl",url.toString());
			
			return ResponseEntity.ok(response);
		} catch(AmazonServiceException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("S3 URL 생성 중 오류 발생: " + e.getMessage());
		}
	}
	
	//1.강의노트 조회 (GET)
	@GetMapping("/{meterialId}/note")
	public ResponseEntity<?> getNote(
			@PathVariable Integer meterialId,
			@RequestParam String stdId){
		Optional<MeterialSubEntity> opt = materialSubRepository.findByMeterialIdAndStdId(meterialId, stdId);
		String note = opt.map(MeterialSubEntity::getContent).orElse("");
		return ResponseEntity.ok(Map.of("content",note));
	}
	
	//2. 강의노트 저장 (POST)
	@PostMapping("/{meterialId}/note")
	public ResponseEntity<?> saveNote(
			@PathVariable Integer meterialId,
			@RequestBody Map<String, String> req){
		String stdId = req.get("stdId");
		String content = req.get("content");
		
		MeterialSubEntity entity = materialSubRepository
				.findByMeterialIdAndStdId(meterialId, stdId)
				.orElseGet(() -> {
					MeterialSubEntity e = new MeterialSubEntity();
					e.setMeterialId(meterialId);
					e.setStdId(stdId);
					return e;
				});
		entity.setContent(content);
		materialSubRepository.save(entity);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("progress/update")
	public ResponseEntity<?> updateProgress(@RequestBody MeterialSubEntity req){
		meterialSubService.updateProgress(req.getMeterialId(), req.getStdId(), req.getProgress());
		return ResponseEntity.ok().body(Map.of("result","ok"));
	}
	
}
