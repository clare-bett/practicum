package com.gxa.cddx.www.forum.service.impl;

import com.gxa.cddx.www.forum.entity.Post;
import com.gxa.cddx.www.forum.entity.PostLike;
import com.gxa.cddx.www.forum.entity.User;
import com.gxa.cddx.www.forum.exception.BusinessException;
import com.gxa.cddx.www.forum.mapper.PostLikeRepository;
import com.gxa.cddx.www.forum.mapper.PostRepository;
import com.gxa.cddx.www.forum.mapper.UserRepository;
import com.gxa.cddx.www.forum.service.PostLikeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 帖子点赞服务实现
 */
@Service
public class PostLikeServiceImpl implements PostLikeService {
    
    private static final Logger log = LoggerFactory.getLogger(PostLikeServiceImpl.class);
    
    @Autowired
    private PostLikeRepository postLikeRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    @Transactional
    public boolean likePost(Long postId, Long userId) {
        // 检查帖子是否存在
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new BusinessException("帖子不存在"));
        
        // 检查用户是否存在
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException("用户不存在"));
        
        // 检查是否已经点赞
        Optional<PostLike> existingLike = postLikeRepository.findByPostIdAndUserId(postId, userId);
        if (existingLike.isPresent()) {
            log.warn("用户 {} 已经点赞过帖子 {}", userId, postId);
            return false;
        }
        
        // 创建点赞记录
        PostLike postLike = new PostLike();
        postLike.setPost(post);
        postLike.setUser(user);
        postLikeRepository.save(postLike);
        
        // 更新帖子的点赞数
        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);
        
        log.info("✅ 用户 {} 点赞帖子 {} 成功", userId, postId);
        return true;
    }
    
    @Override
    @Transactional
    public boolean unlikePost(Long postId, Long userId) {
        // 检查帖子是否存在
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new BusinessException("帖子不存在"));
        
        // 检查是否已经点赞
        Optional<PostLike> existingLike = postLikeRepository.findByPostIdAndUserId(postId, userId);
        if (existingLike.isEmpty()) {
            log.warn("用户 {} 未点赞过帖子 {}", userId, postId);
            return false;
        }
        
        // 删除点赞记录
        postLikeRepository.deleteByPostIdAndUserId(postId, userId);
        
        // 更新帖子的点赞数
        if (post.getLikeCount() > 0) {
            post.setLikeCount(post.getLikeCount() - 1);
            postRepository.save(post);
        }
        
        log.info("✅ 用户 {} 取消点赞帖子 {} 成功", userId, postId);
        return true;
    }
    
    @Override
    public boolean hasLiked(Long postId, Long userId) {
        return postLikeRepository.findByPostIdAndUserId(postId, userId).isPresent();
    }
}

