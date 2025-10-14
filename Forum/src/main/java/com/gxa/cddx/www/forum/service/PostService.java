package com.gxa.cddx.www.forum.service;

import com.gxa.cddx.www.forum.dto.PostDTO;
import com.gxa.cddx.www.forum.dto.PostUpdateDTO;
import com.gxa.cddx.www.forum.vo.PageVO;
import com.gxa.cddx.www.forum.vo.PostVo;

/**
 * 帖子服务接口
 */
public interface PostService {
    
    /**
     * 创建帖子
     */
    PostVo createPost(PostDTO postDTO, Long authorId);
    
    /**
     * 根据ID获取帖子详情
     */
    PostVo getPostById(Long postId);
    
    /**
     * 分页查询帖子列表
     */
    PageVO<PostVo> getPostList(Integer pageNum, Integer pageSize);
    
    /**
     * 根据板块ID分页查询帖子
     */
    PageVO<PostVo> getPostListByCategory(Long categoryId, Integer pageNum, Integer pageSize);
    
    /**
     * 根据作者ID分页查询帖子
     */
    PageVO<PostVo> getPostListByAuthor(Long authorId, Integer pageNum, Integer pageSize);
    
    /**
     * 更新帖子
     * @param postId 帖子ID
     * @param postUpdateDTO 更新数据
     * @param userId 当前用户ID
     * @return 更新后的帖子
     */
    PostVo updatePost(Long postId, PostUpdateDTO postUpdateDTO, Long userId);
    
    /**
     * 删除帖子
     */
    void deletePost(Long postId, Long userId);
    
    /**
     * 增加浏览次数
     */
    void incrementViewCount(Long postId);
    
    /**
     * 管理员获取所有帖子（包括已删除）
     */
    PageVO<PostVo> getAllPostsForAdmin(Integer pageNum, Integer pageSize, Integer status);
    
    /**
     * 管理员删除帖子
     */
    void adminDeletePost(Long postId);
    
    /**
     * 管理员恢复帖子
     */
    void adminRestorePost(Long postId);
    
    /**
     * 根据关键词搜索帖子
     * @param keyword 搜索关键词
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 帖子列表
     */
    PageVO<PostVo> searchPosts(String keyword, Integer pageNum, Integer pageSize);
}

