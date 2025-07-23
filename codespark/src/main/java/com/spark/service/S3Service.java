package com.spark.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3Service{
	private final S3Client s3Client;
	
	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;
	
	@Value("${cloud.aws.credentials.access-key}")
	private String accessKey;

	@Value("${cloud.aws.credentials.secret-key}")
	private String secretKey;
	
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
}