package com.gxa.cddx.www.forum.mapper;

import com.gxa.cddx.www.forum.entity.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 回复数据访问层
 */
@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {
    
    /**
     * 根据帖子ID分页查询回复
     */
    Page<Reply> findByPostIdAndStatusOrderByCreateTimeAsc(
        Long postId, Integer status, Pageable pageable);
    
    /**
     * 根据帖子ID查询回复列表
     */
    List<Reply> findByPostIdAndStatusOrderByCreateTimeAsc(Long postId, Integer status);
    
    /**
     * 根据帖子ID分页查询一级回复（parent为null）
     */
    Page<Reply> findByPostIdAndParentIsNullAndStatusOrderByCreateTimeAsc(
        Long postId, Integer status, Pageable pageable);
    
    /**
     * 根据父回复ID查询子回复
     */
    List<Reply> findByParentIdAndStatusOrderByCreateTimeAsc(Long parentId, Integer status);
    
    /**
     * 统计帖子回复数量
     */
    Long countByPostIdAndStatus(Long postId, Integer status);
}

