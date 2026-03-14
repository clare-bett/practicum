package com.gxa.cddx.www.forum.config;

import com.gxa.cddx.www.forum.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private AuthInterceptor authInterceptor;
    
    /**
     * 跨域配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
    
    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")  // 拦截所有API请求
                .excludePathPatterns(
                        "/api/user/register",    // 注册不需要登录
                        "/api/user/login",       // 登录不需要登录
                        "/api/category/list",    // 查看板块列表不需要登录
                        "/api/category/{categoryId}",  // 查看板块详情不需要登录
                        "/api/post/list",        // 查看帖子列表不需要登录
                        "/api/post/search",      // 搜索帖子不需要登录
                        "/api/post/category/**", // 按板块查询帖子不需要登录
                        "/api/post/author/**",   // 按作者查询帖子不需要登录
                        "/api/reply/post/**",    // 查看回复不需要登录
                        "/api/user/{userId}"     // 查看用户信息不需要登录
                );
    }
}

