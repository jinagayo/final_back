package com.spark.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spark.Entity.MeterialSubEntity;
import com.spark.repository.MeterialSubRepository;

import jakarta.transaction.Transactional;

@Service
public class MeterialSubService {

	@Autowired
	private MeterialSubRepository meterialSubRepository;
	
	public MeterialSubEntity saveOrUpdateSubmission(Integer meterialId, String studentId, String s3Key) {
		Optional<MeterialSubEntity> optional = meterialSubRepository.findByMeterialIdAndStdId(meterialId, studentId);
		
		MeterialSubEntity meterialSub;
		if(optional.isPresent()) {
			//이미 제출한 게 있으면 업데이트
			meterialSub = optional.get();
			meterialSub.setContent(s3Key);
		}else {
			// 없으면 새로 생성
			meterialSub = new MeterialSubEntity();
			meterialSub.setMeterialId(meterialId);
			meterialSub.setStdId(studentId);
			meterialSub.setContent(s3Key);
		}
		return meterialSubRepository.save(meterialSub);
	}

	public MeterialSubEntity getSubmission(Integer meterialId, String studentId) {
		return meterialSubRepository.findByMeterialIdAndStdId( meterialId, studentId).orElse(null);
	}

	@Transactional
	public void updateProgress(int meterialId, String stdId, double progress) {
		MeterialSubEntity entity = meterialSubRepository.findByMeterialIdAndStdId(meterialId, stdId);
				if(entity == null){
				    throw new RuntimeException("not found");
				}
		entity.setProgress(progress);
		meterialSubRepository.save(entity);
		
	}

	public MeterialSubEntity updateComment(Integer metersubId, Integer comment) {
		MeterialSubEntity entity = meterialSubRepository.findById(metersubId)
				.orElseThrow(() -> new RuntimeException("제출물이 존재하지 않습니다."));
		entity.setProgress(comment);
		return meterialSubRepository.save(entity);
	}

	}
