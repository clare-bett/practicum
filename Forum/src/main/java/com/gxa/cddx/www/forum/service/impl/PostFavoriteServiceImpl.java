package com.gxa.cddx.www.forum.service.impl;

import com.gxa.cddx.www.forum.constant.CommonConstant;
import com.gxa.cddx.www.forum.entity.Post;
import com.gxa.cddx.www.forum.entity.PostFavorite;
import com.gxa.cddx.www.forum.entity.User;
import com.gxa.cddx.www.forum.exception.BusinessException;
import com.gxa.cddx.www.forum.mapper.PostFavoriteRepository;
import com.gxa.cddx.www.forum.mapper.PostRepository;
import com.gxa.cddx.www.forum.mapper.UserRepository;
import com.gxa.cddx.www.forum.service.PostFavoriteService;
import com.gxa.cddx.www.forum.vo.PageVO;
import com.gxa.cddx.www.forum.vo.PostVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    @Transactional(readOnly = true)
    public PageVO<PostVo> getMyFavoritedPosts(Long userId, Integer pageNum, Integer pageSize) {
        pageNum = (pageNum == null || pageNum < 1) ? CommonConstant.DEFAULT_PAGE_NUM : pageNum;
        pageSize = (pageSize == null || pageSize < 1) ? CommonConstant.DEFAULT_PAGE_SIZE : pageSize;
        pageSize = Math.min(pageSize, CommonConstant.MAX_PAGE_SIZE);

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        // 只查询未删除帖子的收藏
        Page<PostFavorite> page = postFavoriteRepository.findByUserIdAndPost_StatusOrderByCreateTimeDesc(
                userId, CommonConstant.POST_NORMAL, pageable);

        List<PostVo> records = page.getContent().stream()
                .map(fav -> convertPostToVO(fav.getPost()))
                .collect(Collectors.toList());

        return new PageVO<>(records, page.getTotalElements(), pageNum, pageSize);
    }

    /**
     * 将 Post 转为 PostVo（与帖子列表展示一致）
     */
    private PostVo convertPostToVO(Post post) {
        PostVo vo = new PostVo();
        vo.setId(post.getId());
        vo.setTitle(post.getTitle());
        vo.setContent(post.getContent());
        vo.setCategoryId(post.getCategory().getId());
        vo.setCategoryName(post.getCategory().getName());
        vo.setAuthorId(post.getAuthor().getId());
        vo.setAuthorName(post.getAuthor().getUsername());
        vo.setAuthorAvatar(post.getAuthor().getAvatar());
        vo.setViewCount(post.getViewCount());
        vo.setReplyCount(post.getReplyCount());
        vo.setLikeCount(post.getLikeCount());
        vo.setIsTop(post.getIsTop());
        vo.setIsEssence(post.getIsEssence());
        vo.setStatus(post.getStatus());
        vo.setLastReplyTime(post.getLastReplyTime());
        vo.setCreateTime(post.getCreateTime());
        return vo;
    }
}

