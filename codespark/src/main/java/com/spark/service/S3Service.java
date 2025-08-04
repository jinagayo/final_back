package com.spark.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;
import ws.schild.jave.process.ffmpeg.DefaultFFMPEGLocator;


@Service
@RequiredArgsConstructor
public class S3Service {
	private final S3Client s3Client;
	
	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;
	
	@Value("${cloud.aws.credentials.access-key}")
	private String accessKey;

	@Value("${cloud.aws.credentials.secret-key}")
	private String secretKey;
	
	
	
	public String upload(MultipartFile file, String folderName) throws IOException{
		
		String originalFilename = file.getOriginalFilename();
		String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
	    // 랜덤 파일명(중복 방지)
	    String uuid = UUID.randomUUID().toString();
	    String key = folderName + "/" + uuid + extension;

		
		PutObjectRequest putRequest = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.contentType(file.getContentType())
				.build();
		
		s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
	
			return key;
	}
	//이미지/영상 업로드 용도
	public String generatePresignedUploadUrl(String fileName) {
		S3Presigner presigner = S3Presigner.builder()
				.region(Region.AP_NORTHEAST_2)
				.credentialsProvider(StaticCredentialsProvider.create(
							AwsBasicCredentials.create(accessKey, secretKey)
						))
				.build();
		
		PutObjectRequest objectRequest = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(fileName)
				.contentType("video/mp4")
				.build();
		
		 PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
		            .signatureDuration(Duration.ofMinutes(10))
		            .putObjectRequest(objectRequest)
		            .build();

		        return presigner.presignPutObject(presignRequest).url().toString();
		    }

		//이미지/영상/자료 다운로드·스트리밍 용도
		    public String generatePresignedReadUrl(String fileName){
		        GetObjectRequest objectRequest = GetObjectRequest.builder()
		            .bucket(bucketName)
		            .key(fileName)
		            .build();

		        S3Presigner presigner = S3Presigner.builder()
		            .region(Region.AP_NORTHEAST_2)
		            .credentialsProvider(StaticCredentialsProvider.create(
		                AwsBasicCredentials.create(accessKey, secretKey)
		            ))
		            .build();

		        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
		            .signatureDuration(Duration.ofHours(1))
		            .getObjectRequest(objectRequest)
		            .build();

		        return presigner.presignGetObject(presignRequest).url().toString();
	}
		    
		    /**
		     * 비디오 파일에서 재생 시간을 추출합니다.
		     */
		    public long getVideoDurationInSeconds(File file) throws Exception {
		    
		        MultimediaObject multimediaObject = new MultimediaObject(file);
		        MultimediaInfo info = multimediaObject.getInfo();
		        return info.getDuration() / 1000; // ms → sec
		    }
		 

		    public File downloadVideoFromS3(String key) throws IOException {
		    	GetObjectRequest request = GetObjectRequest.builder()
		    			.bucket(bucketName)
		    			.key(key)
		    			.build();

		    	File tempFile = File.createTempFile("video", ".mp4");

		    	try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(request);
		    			FileOutputStream fos = new FileOutputStream(tempFile)) {
		    		s3Object.transferTo(fos);
		    	}
		    	return tempFile;
}
		
}
