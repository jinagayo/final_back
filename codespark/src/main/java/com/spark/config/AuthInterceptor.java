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
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
	    String requestURI = request.getRequestURI();
	    String requestMethod = request.getMethod();
	    
	    System.out.println("=== Interceptor 실행: " + requestURI + " ===");
	    
	    // OPTIONS 요청은 통과
	    if ("OPTIONS".equals(requestMethod)) {
	        return true;
	    }
	    
	    // 공개 경로는 통과
	    if (isPublicPath(requestURI)) {
	        return true;
	    }
	    
	    // ⭐ 인증이 필요한 경로인지 체크
	    if (!needsAuthentication(requestURI)) {
	        System.out.println("✅ 인증 불필요 경로: " + requestURI);
	        return true;
	    }
	    
	    // 세션 체크
	    HttpSession session = request.getSession(false);
	    if (session == null) {
	        System.out.println("❌ 세션이 없음 - " + requestURI);
	        return handleUnauthorized(response, "로그인이 필요합니다.");
	    }
	    
	    String userId = (String) session.getAttribute("login");
	    if (userId == null) {
	        System.out.println("❌ userId가 null - " + requestURI);
	        return handleUnauthorized(response, "로그인이 필요합니다.");
	    }
	    
	    return true;
	}
	
	private boolean isPublicPath(String uri) {
	    if (uri.equals("/")) {
	        return true;
	    }

	    String[] publicPaths = {
	        "/","/auth/", "/join/", "/login", "/logout", "/home",
	        "/board/", "/course/List", "/course/Detail", "/video/stream",
	        "/api/Mypage/Profile"
	    };
	    for (String path : publicPaths) {
	        if (uri.startsWith(path)) {
	            System.out.println("✅ 공개 경로 매칭: " + path);
	            return true;
	        }
	    }

	    return false;
	}
	
    private boolean needsAuthentication(String uri) {
        return uri.startsWith("/course/teacher/") || uri.startsWith("/video/") || 
               uri.startsWith("/api/") || uri.startsWith("/mypage/") || uri.startsWith("/myclass/");
    }
    
    private boolean handleUnauthorized(HttpServletResponse response,String message) throws IOException {
        response.sendRedirect("/");
        return false;
    }
    
    private boolean handleForbidden(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
        return false;
    }
}
