package com.gxa.cddx.www.forum.service;

import com.gxa.cddx.www.forum.entity.Post;
import com.gxa.cddx.www.forum.entity.Reply;

/**
 * AI服务接口
 */
public interface AIService {
    
    /**
     * 为帖子生成AI回复
     * @param post 帖子对象
     * @return AI生成的回复内容
     */
    String generateReplyForPost(Post post);
    
    /**
     * 为用户的回复生成AI回复（当被@时）
     * @param post 帖子对象
     * @param userReply 用户的回复内容
     * @return AI生成的回复内容
     */
    String generateReplyForMention(Post post, String userReply);
    
    /**
     * 检查AI服务是否可用
     */
    boolean isEnabled();
}

