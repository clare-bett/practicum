package com.gxa.cddx.www.forum.service;

/**
 * 帖子收藏服务接口
 */
public interface PostFavoriteService {
    
    /**
     * 收藏帖子
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean favoritePost(Long postId, Long userId);
    
    /**
     * 取消收藏
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean unfavoritePost(Long postId, Long userId);
    
    /**
     * 检查用户是否收藏了帖子
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否已收藏
     */
    boolean hasFavorited(Long postId, Long userId);
}

