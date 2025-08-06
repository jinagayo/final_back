package com.spark.config;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request,HttpServletResponse response,Object handler) throws Exception{
		String requestURI = request.getRequestURI();
		String requestMethod = request.getMethod();
		
        // OPTIONS 요청은 통과
        if ("OPTIONS".equals(requestMethod)) {
            return true;
        }
        
        // 공개 경로는 통과
        if (isPublicPath(requestURI)) {
            return true;
        }
        
        // 세션에서 사용자 정보 확인
        HttpSession session = request.getSession(false);
        if (session == null) {
            return handleUnauthorized(response, "로그인이 필요합니다.");
        }
        
        String userId = (String) session.getAttribute("login");
        String userRole = (String) session.getAttribute("position");
        
        if (userId == null) {
            return handleUnauthorized(response, "로그인이 필요합니다.");
        }
        
        // 관리자 권한 체크
        if (requestURI.startsWith("/api/admin/")) {
            if (!"3".equals(userRole)) {
                return handleForbidden(response, "관리자 권한이 필요합니다.");
            }
        }
        
        // 강사 권한 체크
        if (requestURI.startsWith("/course/teacher/") || 
            requestURI.startsWith("/video/upload")) {
            if (!"2".equals(userRole) && !"3".equals(userRole)) {
                return handleForbidden(response, "강사 권한이 필요합니다.");
            }
        }
        
        // 일반 인증이 필요한 경로
        if (needsAuthentication(requestURI)) {
            // 이미 userId가 있으므로 통과
            return true;
        }
        
        return true;
	}
	
    private boolean isPublicPath(String uri) {
        String[] publicPaths = {
            "/auth/", "/join/", "/login", "/logout", "/", "/home",
            "/board/", "/course/List", "/course/Detail", "/video/stream"
        };
        
        for (String path : publicPaths) {
            if (uri.startsWith(path)) {
                return true;
            }
        }
        return false;
    }
	
    private boolean needsAuthentication(String uri) {
        return uri.startsWith("/course/") || uri.startsWith("/video/") || 
               uri.startsWith("/api/");
    }
    
    private boolean handleUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
        return false;
    }
    
    private boolean handleForbidden(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
        return false;
    }
}
