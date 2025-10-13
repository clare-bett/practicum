package com.gxa.cddx.www.forum.service.impl;

import com.gxa.cddx.www.forum.service.SensitiveWordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;

/**
 * 敏感词过滤服务实现
 * 使用DFA算法实现高效的敏感词过滤
 */
@Service
public class SensitiveWordServiceImpl implements SensitiveWordService {
    
    private static final Logger log = LoggerFactory.getLogger(SensitiveWordServiceImpl.class);
    
    // 敏感词库（实际项目中应从数据库或配置文件加载）
    private static final Set<String> SENSITIVE_WORDS = new HashSet<>();
    
    // DFA算法的敏感词字典树
    private Map<String, Object> sensitiveWordMap = new HashMap<>();
    
    // 是否结束的标识
    private static final String IS_END = "isEnd";
    
    @PostConstruct
    public void init() {
        // 初始化敏感词库（示例）
        SENSITIVE_WORDS.add("暴力");
        SENSITIVE_WORDS.add("色情");
        SENSITIVE_WORDS.add("赌博");
        SENSITIVE_WORDS.add("毒品");
        SENSITIVE_WORDS.add("枪支");
        SENSITIVE_WORDS.add("反动");
        SENSITIVE_WORDS.add("法轮功");
        SENSITIVE_WORDS.add("政治敏感");
        SENSITIVE_WORDS.add("黄色");
        SENSITIVE_WORDS.add("诈骗");
        SENSITIVE_WORDS.add("侮辱");
        SENSITIVE_WORDS.add("谩骂");
        
        // 构建敏感词字典树
        buildSensitiveWordMap();
        
        log.info("✅ 敏感词过滤服务初始化完成，共加载 {} 个敏感词", SENSITIVE_WORDS.size());
    }
    
    /**
     * 构建敏感词字典树
     */
    @SuppressWarnings("unchecked")
    private void buildSensitiveWordMap() {
        sensitiveWordMap = new HashMap<>();
        
        for (String word : SENSITIVE_WORDS) {
            Map<String, Object> nowMap = sensitiveWordMap;
            
            for (int i = 0; i < word.length(); i++) {
                String key = String.valueOf(word.charAt(i));
                
                // 获取指定节点
                Map<String, Object> wordMap = (Map<String, Object>) nowMap.get(key);
                
                if (wordMap == null) {
                    // 不存在则创建
                    wordMap = new HashMap<>();
                    wordMap.put(IS_END, "0"); // 不是结尾字符
                    nowMap.put(key, wordMap);
                }
                
                nowMap = wordMap;
                
                // 最后一个字符标识为结束
                if (i == word.length() - 1) {
                    nowMap.put(IS_END, "1");
                }
            }
        }
    }
    
    @Override
    public boolean containsSensitiveWord(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        
        for (int i = 0; i < text.length(); i++) {
            int length = checkSensitiveWord(text, i);
            if (length > 0) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public String filterSensitiveWord(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }
        
        StringBuilder result = new StringBuilder(text);
        
        for (int i = 0; i < result.length(); i++) {
            int length = checkSensitiveWord(result.toString(), i);
            if (length > 0) {
                // 替换为***
                for (int j = 0; j < length; j++) {
                    result.setCharAt(i + j, '*');
                }
                i += length - 1; // 跳过已处理的字符
            }
        }
        
        return result.toString();
    }
    
    @Override
    public List<String> getSensitiveWords(String text) {
        List<String> sensitiveWords = new ArrayList<>();
        
        if (text == null || text.trim().isEmpty()) {
            return sensitiveWords;
        }
        
        for (int i = 0; i < text.length(); i++) {
            int length = checkSensitiveWord(text, i);
            if (length > 0) {
                String word = text.substring(i, i + length);
                sensitiveWords.add(word);
                i += length - 1; // 跳过已处理的字符
            }
        }
        
        return sensitiveWords;
    }
    
    /**
     * 检查从指定位置开始是否存在敏感词
     * @param text 待检查文本
     * @param beginIndex 开始索引
     * @return 敏感词长度，0表示不存在
     */
    @SuppressWarnings("unchecked")
    private int checkSensitiveWord(String text, int beginIndex) {
        boolean flag = false;
        int matchLength = 0;
        
        Map<String, Object> nowMap = sensitiveWordMap;
        
        for (int i = beginIndex; i < text.length(); i++) {
            String key = String.valueOf(text.charAt(i));
            
            // 获取指定节点
            nowMap = (Map<String, Object>) nowMap.get(key);
            
            if (nowMap == null) {
                // 不存在，直接返回
                break;
            }
            
            matchLength++;
            
            // 判断是否为最后一个字符
            if ("1".equals(nowMap.get(IS_END))) {
                flag = true;
                break;
            }
        }
        
        return flag ? matchLength : 0;
    }
}

