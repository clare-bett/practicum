package com.gxa.cddx.www.forum.mapper;

import com.gxa.cddx.www.forum.entity.PostFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 帖子收藏Repository
 */
@Repository
public interface PostFavoriteRepository extends JpaRepository<PostFavorite, Long> {
    
    /**
     * 查询用户是否收藏了某个帖子
     */
    Optional<PostFavorite> findByPostIdAndUserId(Long postId, Long userId);
    
    /**
     * 统计帖子的收藏数
     */
    long countByPostId(Long postId);
    
    /**
     * 删除用户对某个帖子的收藏
     */
    void deleteByPostIdAndUserId(Long postId, Long userId);

    /**
     * 分页查询用户收藏的帖子（按收藏时间倒序），可过滤帖子状态
     */
    Page<PostFavorite> findByUserIdAndPost_StatusOrderByCreateTimeDesc(Long userId, Integer postStatus, Pageable pageable);
}

