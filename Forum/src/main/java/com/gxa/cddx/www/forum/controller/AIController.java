package com.gxa.cddx.www.forum.controller;

import com.gxa.cddx.www.forum.annotation.RequireAuth;
import com.gxa.cddx.www.forum.service.AIService;
import com.gxa.cddx.www.forum.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    @PostMapping("/ask")
    @RequireAuth
    public Result<Map<String, String>> ask(@RequestBody Map<String, String> body) {
        String question = body != null ? body.get("question") : null;
        if (question == null || question.trim().isEmpty()) {
            return Result.error("问题不能为空");
        }
        if (!aiService.isEnabled()) {
            return Result.error("AI 服务未启用");
        }
        String answer = aiService.answerFromKnowledge(question.trim());
        if (answer == null) {
            return Result.error("回答生成失败，请稍后重试");
        }
        return Result.success(Map.of("answer", answer));
    }
}
