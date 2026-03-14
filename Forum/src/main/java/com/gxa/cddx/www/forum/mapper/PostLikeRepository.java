package com.gxa.cddx.www.forum.mapper;

import com.gxa.cddx.www.forum.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 帖子点赞Repository
 */
@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    
    /**
     * 查询用户是否点赞了某个帖子
     */
    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);
    
    /**
     * 统计帖子的点赞数
     */
    long countByPostId(Long postId);
    
    /**
     * 删除用户对某个帖子的点赞
     */
    void deleteByPostIdAndUserId(Long postId, Long userId);
}

