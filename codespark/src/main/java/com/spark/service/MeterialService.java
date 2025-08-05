package com.spark.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spark.Entity.MeterialEntity;
import com.spark.repository.MeterialRepository;

import jakarta.transaction.Transactional;

@Service
public class MeterialService {
	
	@Autowired
	MeterialRepository meterialRepo;

	@Transactional
	public int deleteMaterials(Integer classId, List<Integer> meterIds) {
		int count = 0;
		 for(Integer meterId : meterIds) {
	            int deleted = meterialRepo.deleteByClassIdAndMeterId(classId, meterId);
	            count += deleted;
	        }
	        return count;
	}
}
