
package com.spark.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    // PasswordEncoder Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    // CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
    	  CorsConfiguration configuration = new CorsConfiguration();
    	    configuration.setAllowedOrigins(List.of("http://localhost:3000"));  // 반드시 정확한 origin
    	    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    	    configuration.setAllowedHeaders(List.of("*"));
    	    configuration.setAllowCredentials(true);  // 쿠키 전송 허용
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    // Security Filter Chain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // preflight OPTIONS 요청 허용
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                //  회원가입, 로그인, 홈, 기본 경로 허용
                .requestMatchers("/auth/**", "/join/**", "/login", "/", "/home").permitAll()

                //  강의 리스트와 상세페이지는 모두에게 허용
                .requestMatchers("/course/List", "/course/Detail").permitAll()

                // ✅강사만 접근 가능한 영역
                .requestMatchers("/course/teacher/**").hasRole("INSTRUCTOR")

                //  나머지 course는 로그인만 되어 있으면 접근 가능
                .requestMatchers("/course/**").authenticated()

                //  동영상 업로드는 강사만
                .requestMatchers("/video/upload").hasRole("INSTRUCTOR")

                //  영상 전체 접근은 로그인 필요
                .requestMatchers("/video/**").authenticated()

                //  관리자 API 열어둠 (이건 테스트용 주의)
                .requestMatchers("/api/admin/**").permitAll()

                // 나머지는 인증 필요
                .anyRequest().authenticated()
            );

        return http.build();
    }

}