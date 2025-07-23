package com.spark.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.spark.Entity.UserEntity;
import com.spark.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity user = userRepository.findByUserId(username);
		
		String authority = switch(user.getPosition()) {
			case "2" -> "ROLE_INSTRUCTOR";
			case "3" -> "ROLE_STUDENT";
			default -> "ROLE_UNKNOWN";
		};
		
		return User.builder()
				.username(user.getUserId())
				.password(user.getPw())
				.authorities(new SimpleGrantedAuthority(user.getPosition()))
				.build();
		
	}
	
}
