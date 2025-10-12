package com.gxa.cddx.www.forum.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gxa.cddx.www.forum.entity.Post;
import com.gxa.cddx.www.forum.service.AIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * AI服务实现类
 */
@Service
public class AIServiceImpl implements AIService {
    
    private static final Logger log = LoggerFactory.getLogger(AIServiceImpl.class);
    
    @Value("${ai.api.key}")
    private String apiKey;
    
    @Value("${ai.api.url}")
    private String apiUrl;
    
    @Value("${ai.model}")
    private String model;
    
    @Value("${ai.enabled:true}")
    private boolean enabled;
    
    @Value("${ai.reply.enabled:true}")
    private boolean replyEnabled;
    
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public String generateReplyForPost(Post post) {
        if (!isEnabled() || !replyEnabled) {
            return null;
        }
        
        try {
            // 构建提示词
            String prompt = buildPromptForPost(post);
            
            // 构建请求体
            String requestBody = String.format("""
                {
                  "model": "%s",
                  "messages": [
                    {
                      "role": "system",
                      "content": "你是一个毒舌、直率的论坛老用户。你看不惯那些问题太简单、思考不充分的帖子。你的回复应该：1. 简洁尖锐 2. 直指问题要害 3. 语气略带嘲讽但不过分 4. 字数控制在100-200字 5. 偶尔用'这种问题也问？'、'建议先动手试试再来提问'之类的开场。不要使用'作为AI助手'这样的表述，要像一个资深用户在教训新手一样。"
                    },
                    {
                      "role": "user",
                      "content": "%s"
                    }
                  ],
                  "max_tokens": 300,
                  "temperature": 0.8
                }
                """, model, prompt.replace("\"", "\\\"").replace("\n", "\\n"));
            
            // 创建HTTP请求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/chat/completions"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(60))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            
            // 发送请求
            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            // 解析响应
            if (response.statusCode() == 200) {
                JsonNode jsonResponse = objectMapper.readTree(response.body());
                String content = jsonResponse
                        .path("choices")
                        .get(0)
                        .path("message")
                        .path("content")
                        .asText();
                return content.trim();
            } else {
                log.error("AI API调用失败: {} - {}", response.statusCode(), response.body());
                return null;
            }
            
        } catch (Exception e) {
            log.error("AI回复生成失败: {}", e.getMessage());
            return null;
        }
    }
    
    @Override
    public String generateReplyForMention(Post post, String userReply) {
        if (!isEnabled()) {
            log.debug("AI功能未启用");
            return null;
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            // 构建针对用户回复的提示词
            String prompt = buildPromptForMention(post, userReply);
            
            // 构建请求体
            String requestBody = String.format("""
                {
                  "model": "%s",
                  "messages": [
                    {
                      "role": "system",
                      "content": "你是一个毒舌、直率的论坛老手。用户@了你，说明他遇到问题了。你的回复应该：1. 直接点出用户的问题所在 2. 语气略带嘲讽但给出解决方案 3. 可以用'又是这种低级错误'、'看来你没认真看文档'、'自己动手试试不就知道了？'之类的开场 4. 字数控制在100-200字。尽管语气刻薄，但最终还是要给出实用的建议。不要使用'作为AI助手'这样的表述。"
                    },
                    {
                      "role": "user",
                      "content": "%s"
                    }
                  ],
                  "max_tokens": 300,
                  "temperature": 0.8
                }
                """, model, prompt.replace("\"", "\\\"").replace("\n", "\\n"));
            
            // 创建HTTP请求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/chat/completions"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(60))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            
            // 发送请求
            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            long apiTime = System.currentTimeMillis() - startTime;
            log.info("AI API响应时间: {}ms, 状态码: {}", apiTime, response.statusCode());
            
            // 解析响应
            if (response.statusCode() == 200) {
                JsonNode jsonResponse = objectMapper.readTree(response.body());
                String content = jsonResponse
                        .path("choices")
                        .get(0)
                        .path("message")
                        .path("content")
                        .asText();
                
                log.info("AI回复生成成功，长度: {} 字符", content.length());
                return content.trim();
            } else {
                log.error("AI API调用失败，状态码: {}, 响应体: {}", response.statusCode(), response.body());
                return null;
            }
            
        } catch (java.net.http.HttpTimeoutException e) {
            long timeout = System.currentTimeMillis() - startTime;
            log.error("AI API超时（{}ms）: {}", timeout, e.getMessage());
            return null;
        } catch (java.net.ConnectException e) {
            log.error("无法连接到AI服务: {}, API地址: {}", e.getMessage(), apiUrl);
            return null;
        } catch (Exception e) {
            long errorTime = System.currentTimeMillis() - startTime;
            log.error("AI回复生成失败（耗时{}ms）: {} - {}", errorTime, e.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * 为帖子构建提示词
     */
    private String buildPromptForPost(Post post) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请为以下论坛帖子提供一条有价值的回复：\\n\\n");
        prompt.append("【标题】").append(post.getTitle()).append("\\n\\n");
        prompt.append("【内容】").append(post.getContent()).append("\\n\\n");
        prompt.append("请生成一条友好、有帮助的回复：");
        return prompt.toString();
    }
    
    /**
     * 为用户的@提及构建提示词
     */
    private String buildPromptForMention(Post post, String userReply) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("用户在以下帖子中@了你：\\n\\n");
        prompt.append("【帖子标题】").append(post.getTitle()).append("\\n\\n");
        prompt.append("【帖子内容】").append(post.getContent()).append("\\n\\n");
        prompt.append("【用户的回复】").append(userReply.replace("@AI助手", "").replace("@AI", "").trim()).append("\\n\\n");
        prompt.append("请针对用户的回复内容，给出友好、有帮助的回应：");
        return prompt.toString();
    }
}

