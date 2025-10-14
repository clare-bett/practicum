package com.gxa.cddx.www.forum.mapper;

import com.gxa.cddx.www.forum.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 帖子数据访问层
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    /**
     * 根据板块ID分页查询帖子
     */
    Page<Post> findByCategoryIdAndStatusOrderByIsTopDescCreateTimeDesc(
        Long categoryId, Integer status, Pageable pageable);
    
    /**
     * 根据作者ID分页查询帖子
     */
    Page<Post> findByAuthorIdAndStatusOrderByCreateTimeDesc(
        Long authorId, Integer status, Pageable pageable);
    
    /**
     * 分页查询所有正常状态的帖子
     */
    Page<Post> findByStatusOrderByIsTopDescCreateTimeDesc(Integer status, Pageable pageable);
    
    /**
     * 根据状态分页查询帖子（管理员用）
     */
    Page<Post> findByStatus(Integer status, Pageable pageable);
    
    /**
     * 增加浏览次数
     */
    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = ?1")
    void incrementViewCount(Long postId);
    
    /**
     * 增加回复数量
     */
    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.replyCount = p.replyCount + 1 WHERE p.id = ?1")
    void incrementReplyCount(Long postId);
    
    /**
     * 根据标题或内容模糊搜索帖子（支持分页）
     */
    @Query("SELECT p FROM Post p WHERE p.status = ?3 AND (p.title LIKE %?1% OR p.content LIKE %?2%) ORDER BY p.isTop DESC, p.createTime DESC")
    Page<Post> searchByTitleOrContent(String titleKeyword, String contentKeyword, Integer status, Pageable pageable);
}

