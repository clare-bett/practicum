package com.gxa.cddx.www.forum.controller;

import com.gxa.cddx.www.forum.annotation.RequireAuth;
import com.gxa.cddx.www.forum.service.PostFavoriteService;
import com.gxa.cddx.www.forum.service.PostLikeService;
import com.gxa.cddx.www.forum.vo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 互动管理控制器（点赞、收藏）
 */
@RestController
@RequestMapping("/api/interaction")
public class InteractionController {
    
    private static final Logger log = LoggerFactory.getLogger(InteractionController.class);
    
    @Autowired
    private PostLikeService postLikeService;
    
    @Autowired
    private PostFavoriteService postFavoriteService;
    
    /**
     * 点赞帖子
     */
    @PostMapping("/like/{postId}")
    @RequireAuth
    public Result<?> likePost(@PathVariable Long postId, @RequestAttribute("userId") Long userId) {
        boolean success = postLikeService.likePost(postId, userId);
        if (success) {
            return Result.success("点赞成功");
        } else {
            return Result.error("已经点赞过了");
        }
    }
    
    /**
     * 取消点赞
     */
    @DeleteMapping("/like/{postId}")
    @RequireAuth
    public Result<?> unlikePost(@PathVariable Long postId, @RequestAttribute("userId") Long userId) {
        boolean success = postLikeService.unlikePost(postId, userId);
        if (success) {
            return Result.success("取消点赞成功");
        } else {
            return Result.error("未点赞过");
        }
    }
    
    /**
     * 检查是否已点赞
     */
    @GetMapping("/like/check/{postId}")
    @RequireAuth
    public Result<Boolean> checkLike(@PathVariable Long postId, @RequestAttribute("userId") Long userId) {
        boolean hasLiked = postLikeService.hasLiked(postId, userId);
        return Result.success(hasLiked);
    }
    
    /**
     * 收藏帖子
     */
    @PostMapping("/favorite/{postId}")
    @RequireAuth
    public Result<?> favoritePost(@PathVariable Long postId, @RequestAttribute("userId") Long userId) {
        boolean success = postFavoriteService.favoritePost(postId, userId);
        if (success) {
            return Result.success("收藏成功");
        } else {
            return Result.error("已经收藏过了");
        }
    }
    
    /**
     * 取消收藏
     */
    @DeleteMapping("/favorite/{postId}")
    @RequireAuth
    public Result<?> unfavoritePost(@PathVariable Long postId, @RequestAttribute("userId") Long userId) {
        boolean success = postFavoriteService.unfavoritePost(postId, userId);
        if (success) {
            return Result.success("取消收藏成功");
        } else {
            return Result.error("未收藏过");
        }
    }
    
    /**
     * 检查是否已收藏
     */
    @GetMapping("/favorite/check/{postId}")
    @RequireAuth
    public Result<Boolean> checkFavorite(@PathVariable Long postId, @RequestAttribute("userId") Long userId) {
        boolean hasFavorited = postFavoriteService.hasFavorited(postId, userId);
        return Result.success(hasFavorited);
    }
}

