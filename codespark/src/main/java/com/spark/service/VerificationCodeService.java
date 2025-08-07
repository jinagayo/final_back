package com.spark.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class VerificationCodeService {
		private final Map<String,String> codeStore = new ConcurrentHashMap<>();
		
		//인증코드 저장
		public void saveCode(String userId, String code) {
			codeStore.put(userId, code);
		}

		public boolean verifyCode(String userId, String verificationCode) {
			return verificationCode.equals(codeStore.get(userId));
		}

		public void removeCode(String userId) {
			codeStore.remove(userId);
		}
}
