package com.spark.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors().and()
            .csrf().disable()
            .authorizeHttpRequests((auth) -> auth
                .requestMatchers("/join/**").permitAll()  // ✅ 여기에 join 경로 허용
                .anyRequest().authenticated()
            );
        return http.build();
    }
}