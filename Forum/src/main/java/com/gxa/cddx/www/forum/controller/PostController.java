package com.gxa.cddx.www.forum.controller;

import com.gxa.cddx.www.forum.annotation.RequireAuth;
import com.gxa.cddx.www.forum.dto.PostDTO;
import com.gxa.cddx.www.forum.dto.PostUpdateDTO;
import com.gxa.cddx.www.forum.service.PostService;
import com.gxa.cddx.www.forum.vo.PageVO;
import com.gxa.cddx.www.forum.vo.PostVo;
import com.gxa.cddx.www.forum.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 帖子控制器
 */
@RestController
@RequestMapping("/api/post")
@Validated
public class PostController {
    
    @Autowired
    private PostService postService;
    
    /**
     * 创建帖子
     */
    @RequireAuth
    @PostMapping
    public Result<PostVo> createPost(@Valid @RequestBody PostDTO postDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        PostVo postVO = postService.createPost(postDTO, userId);
        return Result.success("发帖成功", postVO);
    }
    
    /**
     * 获取帖子详情
     */
    @GetMapping("/{postId}")
    public Result<PostVo> getPostById(@PathVariable Long postId) {
        PostVo postVO = postService.getPostById(postId);
        return Result.success(postVO);
    }
    
    /**
     * 分页查询帖子列表
     */
    @GetMapping("/list")
    public Result<PageVO<PostVo>> getPostList(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        PageVO<PostVo> pageVO = postService.getPostList(pageNum, pageSize);
        return Result.success(pageVO);
    }
    
    /**
     * 根据板块ID分页查询帖子
     */
    @GetMapping("/category/{categoryId}")
    public Result<PageVO<PostVo>> getPostListByCategory(
            @PathVariable Long categoryId,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        PageVO<PostVo> pageVO = postService.getPostListByCategory(categoryId, pageNum, pageSize);
        return Result.success(pageVO);
    }
    
    /**
     * 根据作者ID分页查询帖子
     */
    @GetMapping("/author/{authorId}")
    public Result<PageVO<PostVo>> getPostListByAuthor(
            @PathVariable Long authorId,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        PageVO<PostVo> pageVO = postService.getPostListByAuthor(authorId, pageNum, pageSize);
        return Result.success(pageVO);
    }
    
    /**
     * 更新帖子
     */
    @RequireAuth
    @PutMapping("/{postId}")
    public Result<PostVo> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateDTO postUpdateDTO,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        PostVo postVO = postService.updatePost(postId, postUpdateDTO, userId);
        return Result.success("编辑成功", postVO);
    }
    
    /**
     * 删除帖子
     */
    @RequireAuth
    @DeleteMapping("/{postId}")
    public Result<Void> deletePost(@PathVariable Long postId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("用户未登录，请重新登录");
        }
        postService.deletePost(postId, userId);
        return Result.success("删除成功", null);
    }
    
    /**
     * 管理员获取所有帖子（包括已删除）
     */
    @RequireAuth(admin = true)
    @GetMapping("/admin/list")
    public Result<PageVO<PostVo>> getAllPostsForAdmin(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Integer status) {
        PageVO<PostVo> pageVO = postService.getAllPostsForAdmin(pageNum, pageSize, status);
        return Result.success(pageVO);
    }
    
    /**
     * 管理员删除帖子
     */
    @RequireAuth(admin = true)
    @DeleteMapping("/admin/{postId}")
    public Result<Void> adminDeletePost(@PathVariable Long postId) {
        postService.adminDeletePost(postId);
        return Result.success("删除成功", null);
    }
    
    /**
     * 管理员恢复帖子
     */
    @RequireAuth(admin = true)
    @PutMapping("/admin/{postId}/restore")
    public Result<Void> adminRestorePost(@PathVariable Long postId) {
        postService.adminRestorePost(postId);
        return Result.success("恢复成功", null);
    }
    
    /**
     * 搜索帖子（支持标题和内容模糊搜索）
     */
    @GetMapping("/search")
    public Result<PageVO<PostVo>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        PageVO<PostVo> pageVO = postService.searchPosts(keyword, pageNum, pageSize);
        return Result.success(pageVO);
    }
}

