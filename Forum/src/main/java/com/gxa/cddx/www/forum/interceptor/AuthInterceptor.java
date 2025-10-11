package com.gxa.cddx.www.forum.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gxa.cddx.www.forum.annotation.RequireAuth;
import com.gxa.cddx.www.forum.constant.CommonConstant;
import com.gxa.cddx.www.forum.constant.ResultCode;
import com.gxa.cddx.www.forum.utils.JwtUtil;
import com.gxa.cddx.www.forum.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证拦截器
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是方法处理器，直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        
        // 检查方法或类上是否有@RequireAuth注解
        RequireAuth methodAuth = handlerMethod.getMethodAnnotation(RequireAuth.class);
        RequireAuth classAuth = handlerMethod.getBeanType().getAnnotation(RequireAuth.class);
        
        // 如果都没有注解，直接放行
        if (methodAuth == null && classAuth == null) {
            return true;
        }
        
        // 获取实际的注解（方法注解优先于类注解）
        RequireAuth requireAuth = methodAuth != null ? methodAuth : classAuth;
        
        // 从请求头获取Token
        String token = request.getHeader("Authorization");
        
        // 如果没有Token或格式不对
        if (token == null || !token.startsWith("Bearer ")) {
            writeErrorResponse(response, ResultCode.UNAUTHORIZED, "请先登录");
            return false;
        }
        
        // 去除"Bearer "前缀
        token = token.substring(7);
        
        // 验证Token
        if (!JwtUtil.validateToken(token)) {
            writeErrorResponse(response, ResultCode.UNAUTHORIZED, "登录已过期，请重新登录");
            return false;
        }
        
        // 获取用户信息
        Long userId = JwtUtil.getUserIdFromToken(token);
        String username = JwtUtil.getUsernameFromToken(token);
        String role = JwtUtil.getRoleFromToken(token);
        
        // 检查userId是否为null
        if (userId == null) {
            writeErrorResponse(response, ResultCode.UNAUTHORIZED, "Token无效，无法获取用户信息");
            return false;
        }
        
        // 将用户信息存储到请求属性中，供Controller使用
        request.setAttribute("userId", userId);
        request.setAttribute("username", username);
        request.setAttribute("role", role);
        
        // 如果需要管理员权限
        if (requireAuth.admin() && !CommonConstant.ROLE_ADMIN.equals(role)) {
            writeErrorResponse(response, ResultCode.FORBIDDEN, "需要管理员权限");
            return false;
        }
        
        return true;
    }
    
    /**
     * 写入错误响应
     */
    private void writeErrorResponse(HttpServletResponse response, int code, String message) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        Result<?> result = Result.error(code, message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}

