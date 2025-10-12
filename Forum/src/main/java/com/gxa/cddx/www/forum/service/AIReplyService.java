package com.gxa.cddx.www.forum.service;

import com.gxa.cddx.www.forum.entity.Post;

/**
 * AI回复服务接口
 * 专门用于处理异步AI回复生成
 */
public interface AIReplyService {
    
    /**
     * 异步生成AI回复（针对@提及）
     * @param postId 帖子ID
     * @param userReplyContent 用户回复内容
     * @param userReplyId 用户回复ID
     */
    void generateAIReplyForMentionAsync(Long postId, String userReplyContent, Long userReplyId);
}

