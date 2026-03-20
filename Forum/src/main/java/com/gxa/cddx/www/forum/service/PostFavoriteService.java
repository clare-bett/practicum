package com.gxa.cddx.www.forum.service;

import com.gxa.cddx.www.forum.vo.PageVO;
import com.gxa.cddx.www.forum.vo.PostVo;

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

    /**
     * 分页查询当前用户收藏的帖子列表
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 分页结果（仅包含未删除的帖子）
     */
    PageVO<PostVo> getMyFavoritedPosts(Long userId, Integer pageNum, Integer pageSize);
}

