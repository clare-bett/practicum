package com.gxa.cddx.www.forum.service;

/**
 * 帖子点赞服务接口
 */
public interface PostLikeService {
    
    /**
     * 点赞帖子
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean likePost(Long postId, Long userId);
    
    /**
     * 取消点赞
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean unlikePost(Long postId, Long userId);
    
    /**
     * 检查用户是否点赞了帖子
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    boolean hasLiked(Long postId, Long userId);
}

