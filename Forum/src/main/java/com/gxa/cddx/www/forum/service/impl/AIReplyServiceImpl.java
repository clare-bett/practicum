package com.gxa.cddx.www.forum.service.impl;

import com.gxa.cddx.www.forum.constant.CommonConstant;
import com.gxa.cddx.www.forum.entity.Post;
import com.gxa.cddx.www.forum.entity.Reply;
import com.gxa.cddx.www.forum.entity.User;
import com.gxa.cddx.www.forum.mapper.PostRepository;
import com.gxa.cddx.www.forum.mapper.ReplyRepository;
import com.gxa.cddx.www.forum.mapper.UserRepository;
import com.gxa.cddx.www.forum.service.AIReplyService;
import com.gxa.cddx.www.forum.service.AIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * AI回复服务实现类
 * 独立的Service，确保@Async和事务正常工作
 */
@Service
public class AIReplyServiceImpl implements AIReplyService {
    
    private static final Logger log = LoggerFactory.getLogger(AIReplyServiceImpl.class);
    
    @Autowired
    private AIService aiService;
    
    @Autowired
    private ReplyRepository replyRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 异步生成AI回复（针对@提及）
     * 此方法在主事务提交后才被调用（通过TransactionSynchronization）
     * 使用REQUIRES_NEW传播级别，创建独立的事务
     */
    @Override
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void generateAIReplyForMentionAsync(Long postId, String userReplyContent, Long userReplyId) {
        long startTime = System.currentTimeMillis();
        
        try {
            log.info("开始生成AI回复，帖子ID: {}, 用户回复ID: {}", postId, userReplyId);
            
            // 重新查询帖子，确保在当前事务中
            Post post = postRepository.findById(postId).orElse(null);
            if (post == null) {
                log.error("帖子不存在: {}", postId);
                return;
            }
            
            // 调用AI服务生成回复
            String aiContent = aiService.generateReplyForMention(post, userReplyContent);
            
            if (aiContent == null || aiContent.isEmpty()) {
                log.error("AI回复生成失败（内容为空）");
                return;
            }
            
            // 获取或创建AI助手用户
            User aiUser = getOrCreateAIUser();
            
            // 获取用户的回复（父回复）
            Reply parentReply = replyRepository.findById(userReplyId).orElse(null);
            if (parentReply == null) {
                log.error("用户回复不存在: {}", userReplyId);
                return;
            }
            
            // 创建AI回复（二级回复，回复到用户的回复下）
            Reply aiReply = new Reply();
            aiReply.setPost(post);
            aiReply.setUser(aiUser);
            aiReply.setContent(aiContent);
            aiReply.setStatus(CommonConstant.STATUS_NORMAL);
            aiReply.setIsAi(1); // 标记为AI回复
            
            // 关键：设置父回复为用户的回复
            // 如果用户回复本身是二级回复，则应该回复到其父回复（一级回复）下
            if (parentReply.getParent() != null) {
                // 用户回复是二级回复，AI也回复到同一个一级回复下
                aiReply.setParent(parentReply.getParent());
                aiReply.setReplyToUser(parentReply.getUser());
            } else {
                // 用户回复是一级回复，AI作为其二级回复
                aiReply.setParent(parentReply);
                aiReply.setReplyToUser(parentReply.getUser());
            }
            
            replyRepository.save(aiReply);
            
            // 增加帖子回复数
            postRepository.incrementReplyCount(post.getId());
            
            // 更新帖子最后回复时间
            post.setLastReplyTime(LocalDateTime.now());
            postRepository.save(post);
            
            long totalTime = System.currentTimeMillis() - startTime;
            log.info("AI回复保存成功，耗时: {}ms", totalTime);
            
        } catch (Exception e) {
            long errorTime = System.currentTimeMillis() - startTime;
            log.error("生成AI回复失败，耗时: {}ms, 错误: {}", errorTime, e.getMessage());
        }
    }
    
    /**
     * 获取或创建AI助手用户
     */
    private User getOrCreateAIUser() {
        // 先尝试通过用户名查找AI助手
        User existingAI = userRepository.findByUsername("AI助手").orElse(null);
        
        if (existingAI != null) {
            return existingAI;
        }
        
        // 如果不存在，创建新的AI用户（不手动设置ID，让数据库自动生成）
        User aiUser = new User();
        aiUser.setUsername("AI助手");
        aiUser.setPassword(""); // AI用户无需密码
        aiUser.setEmail("ai@forum.com");
        aiUser.setNickname("AI智能助手");
        aiUser.setAvatar("https://img.icons8.com/color/96/artificial-intelligence.png");
        aiUser.setBio("我是AI智能助手，随时为您解答问题。");
        aiUser.setRole(CommonConstant.ROLE_USER);
        aiUser.setStatus(CommonConstant.STATUS_NORMAL);
        
        try {
            return userRepository.save(aiUser);
        } catch (Exception e) {
            // 如果保存失败（可能是并发创建），再次查询
            log.warn("创建AI用户失败，尝试重新查询");
            return userRepository.findByUsername("AI助手")
                    .orElseThrow(() -> new RuntimeException("无法获取或创建AI用户"));
        }
    }
}

