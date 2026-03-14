package com.gxa.cddx.www.forum.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gxa.cddx.www.forum.entity.KnowledgeDocument;
import com.gxa.cddx.www.forum.entity.Post;
import com.gxa.cddx.www.forum.service.AIService;
import com.gxa.cddx.www.forum.service.KnowledgeDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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

    @org.springframework.beans.factory.annotation.Autowired
    private KnowledgeDocumentService knowledgeDocumentService;

    private static final int RAG_DOC_SNIPPET_LEN = 600;

    /** 回复风格：自然、亲切，像学长学姐在帮忙，同时说清信息来源 */
    private static final String SYSTEM_PROMPT_RAG = "你是校园论坛里的一位热心助手，语气像学长学姐跟学弟学妹聊天，自然、亲切、不刻板。"
        + "回答时请以下面【知识库】为准：有相关条目就主要根据知识库来答，可以自然带一句「根据学校/知识库里的信息」之类的；"
        + "如果知识库只有部分相关内容（比如只有寒假没有暑假），就根据已有的说，再顺带提一句「别的假期/事项目前知识库还没有，有需要可以再问学校」；"
        + "如果知识库没有相关条目，就简单说一句「这块知识库暂时没有，下面是一点一般性的建议哈」再回答，不要装成官方口径。"
        + "整体 150～300 字，口语化一点，少用「您」「敬请」这类太正式的词，可以用「你」「哈」「哦」让回复更自然。";

    private static final String SYSTEM_PROMPT_EXPAND = "你是一个检索关键词扩展助手。根据用户问题，列出3-6个适合在校园知识库中检索的关键词。例如：问「放假」可写寒假、暑假、假期、校历；问「开学」可写开学、报到、注册。只输出关键词，用中文逗号或英文逗号分隔，不要编号不要解释。";

    @Override
    public String answerFromKnowledge(String question) {
        if (!isEnabled() || question == null || question.trim().isEmpty()) return null;
        try {
            List<KnowledgeDocument> docs = retrieveWithExpandedKeywords(question.trim(), 5);
            String context = buildKnowledgeContext(docs);
            String userContent = context + "\n\n【用户问题】" + question.trim() + "\n\n根据上面知识库和用户问题，用自然口吻回复一下～";
            return callLlm(SYSTEM_PROMPT_RAG, userContent);
        } catch (Exception e) {
            log.error("知识库问答失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 先用原问句检索；若无结果则用 AI 扩展出相关检索词（如「放假」→「寒假、暑假、假期」）再检索并合并。
     */
    private List<KnowledgeDocument> retrieveWithExpandedKeywords(String query, int maxResults) {
        List<KnowledgeDocument> docs = knowledgeDocumentService.searchByKeyword(query, maxResults);
        if (!docs.isEmpty()) return docs;
        List<String> expanded = expandSearchKeywords(query);
        if (expanded.isEmpty()) return List.of();
        Set<String> all = new LinkedHashSet<>();
        all.add(query);
        all.addAll(expanded);
        return knowledgeDocumentService.searchByKeywords(new ArrayList<>(all), maxResults);
    }

    /** 用 AI 将用户问题扩展为多个相关检索词，便于「放假」命中「寒假」等条目 */
    private List<String> expandSearchKeywords(String userQuestion) {
        if (userQuestion == null || userQuestion.trim().isEmpty()) return List.of();
        try {
            String raw = callLlmWithMaxTokens(SYSTEM_PROMPT_EXPAND, "用户问题：「" + userQuestion.trim() + "」。请输出检索关键词，逗号分隔。", 80);
            if (raw == null || raw.trim().isEmpty()) return List.of();
            String[] parts = raw.split("[,，、\\s]+");
            List<String> list = new ArrayList<>();
            for (String p : parts) {
                String t = p.trim().replaceAll("^[0-9]+[.．、]\\s*", "");
                if (t.length() >= 1 && t.length() <= 20) list.add(t);
            }
            return list.stream().limit(6).toList();
        } catch (Exception e) {
            log.debug("检索词扩展失败，将仅用原问题检索: {}", e.getMessage());
            return List.of();
        }
    }

    private String callLlmWithMaxTokens(String systemPrompt, String userContent, int maxTokens) throws Exception {
        String body = String.format("""
            {
              "model": "%s",
              "messages": [
                { "role": "system", "content": "%s" },
                { "role": "user", "content": "%s" }
              ],
              "max_tokens": %d,
              "temperature": 0.3
            }
            """, model, escapeJson(systemPrompt), escapeJson(userContent), maxTokens);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/chat/completions"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200) return null;
        JsonNode root = objectMapper.readTree(res.body());
        return root.path("choices").get(0).path("message").path("content").asText().trim();
    }

    private String buildKnowledgeContext(java.util.List<KnowledgeDocument> docs) {
        if (docs == null || docs.isEmpty()) return "【知识库】暂无相关条目。";
        StringBuilder sb = new StringBuilder("【知识库】\n\n");
        for (int i = 0; i < docs.size(); i++) {
            KnowledgeDocument d = docs.get(i);
            String title = d.getTitle() != null ? d.getTitle() : "";
            String cat = d.getCategory() != null ? d.getCategory() : "";
            String content = d.getContent() != null ? d.getContent() : "";
            if (content.length() > RAG_DOC_SNIPPET_LEN) content = content.substring(0, RAG_DOC_SNIPPET_LEN) + "...";
            sb.append("--- 条目").append(i + 1).append(" ---\n");
            if (!cat.isEmpty()) sb.append("分类：").append(cat).append("\n");
            sb.append("标题：").append(title).append("\n");
            sb.append("内容：").append(content).append("\n\n");
        }
        return sb.toString();
    }

    private String callLlm(String systemPrompt, String userContent) throws Exception {
        String body = String.format("""
            {
              "model": "%s",
              "messages": [
                { "role": "system", "content": "%s" },
                { "role": "user", "content": "%s" }
              ],
              "max_tokens": 500,
              "temperature": 0.6
            }
            """, model,
            escapeJson(systemPrompt),
            escapeJson(userContent));
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/chat/completions"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(60))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200) {
            log.error("AI API 错误: {} - {}", res.statusCode(), res.body());
            return null;
        }
        JsonNode root = objectMapper.readTree(res.body());
        return root.path("choices").get(0).path("message").path("content").asText().trim();
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
    
    @Override
    public String generateReplyForPost(Post post) {
        if (!isEnabled() || !replyEnabled) {
            return null;
        }
        
        try {
            String query = (post.getTitle() != null ? post.getTitle() : "") + " " + truncate(post.getContent(), 120);
            List<KnowledgeDocument> docs = retrieveWithExpandedKeywords(query.trim(), 5);
            String knowledgeContext = buildKnowledgeContext(docs);
            String userContent = knowledgeContext + "\n\n【帖子标题】" + post.getTitle() + "\n\n【帖子内容】" + post.getContent() + "\n\n根据上面知识库和帖子内容，用自然口吻写一条回复（150-250字），像在跟发帖人聊天一样～";
            return callLlm(SYSTEM_PROMPT_RAG, userContent);
        } catch (Exception e) {
            log.error("AI回复生成失败: {}", e.getMessage());
            return null;
        }
    }

    private static String truncate(String s, int maxLen) {
        if (s == null) return "";
        return s.length() <= maxLen ? s : s.substring(0, maxLen) + "...";
    }
    
    @Override
    public String generateReplyForMention(Post post, String userReply) {
        if (!isEnabled()) {
            log.debug("AI功能未启用");
            return null;
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            String userQuestion = userReply.replace("@AI助手", "").replace("@AI", "").trim();
            List<KnowledgeDocument> docs = retrieveWithExpandedKeywords(userQuestion, 5);
            String knowledgeContext = buildKnowledgeContext(docs);
            String userContent = knowledgeContext + "\n\n【当前帖子标题】" + post.getTitle() + "\n\n【帖子内容】" + post.getContent() + "\n\n【用户的问题】" + userQuestion + "\n\n根据上面知识库和用户问题，用自然口吻回复用户～";
            String answer = callLlm(SYSTEM_PROMPT_RAG, userContent);
            long apiTime = System.currentTimeMillis() - startTime;
            log.info("AI API响应时间: {}ms", apiTime);
            return answer;
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
}

