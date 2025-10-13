package com.gxa.cddx.www.forum.service.impl;

import com.gxa.cddx.www.forum.entity.Post;
import com.gxa.cddx.www.forum.entity.PostFavorite;
import com.gxa.cddx.www.forum.entity.User;
import com.gxa.cddx.www.forum.exception.BusinessException;
import com.gxa.cddx.www.forum.mapper.PostFavoriteRepository;
import com.gxa.cddx.www.forum.mapper.PostRepository;
import com.gxa.cddx.www.forum.mapper.UserRepository;
import com.gxa.cddx.www.forum.service.PostFavoriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 帖子收藏服务实现
 */
@Service
public class PostFavoriteServiceImpl implements PostFavoriteService {
    
    private static final Logger log = LoggerFactory.getLogger(PostFavoriteServiceImpl.class);
    
    @Autowired
    private PostFavoriteRepository postFavoriteRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    @Transactional
    public boolean favoritePost(Long postId, Long userId) {
        // 检查帖子是否存在
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new BusinessException("帖子不存在"));
        
        // 检查用户是否存在
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException("用户不存在"));
        
        // 检查是否已经收藏
        Optional<PostFavorite> existingFavorite = postFavoriteRepository.findByPostIdAndUserId(postId, userId);
        if (existingFavorite.isPresent()) {
            log.warn("用户 {} 已经收藏过帖子 {}", userId, postId);
            return false;
        }
        
        // 创建收藏记录
        PostFavorite postFavorite = new PostFavorite();
        postFavorite.setPost(post);
        postFavorite.setUser(user);
        postFavoriteRepository.save(postFavorite);
        
        log.info("✅ 用户 {} 收藏帖子 {} 成功", userId, postId);
        return true;
    }
    
    @Override
    @Transactional
    public boolean unfavoritePost(Long postId, Long userId) {
        // 检查是否已经收藏
        Optional<PostFavorite> existingFavorite = postFavoriteRepository.findByPostIdAndUserId(postId, userId);
        if (existingFavorite.isEmpty()) {
            log.warn("用户 {} 未收藏过帖子 {}", userId, postId);
            return false;
        }
        
        // 删除收藏记录
        postFavoriteRepository.deleteByPostIdAndUserId(postId, userId);
        
        log.info("✅ 用户 {} 取消收藏帖子 {} 成功", userId, postId);
        return true;
    }
    
    @Override
    public boolean hasFavorited(Long postId, Long userId) {
        return postFavoriteRepository.findByPostIdAndUserId(postId, userId).isPresent();
    }
}

