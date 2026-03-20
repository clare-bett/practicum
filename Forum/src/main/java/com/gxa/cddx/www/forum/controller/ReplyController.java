package com.gxa.cddx.www.forum.controller;

import com.gxa.cddx.www.forum.annotation.RequireAuth;
import com.gxa.cddx.www.forum.dto.ReplyDTO;
import com.gxa.cddx.www.forum.service.ReplyService;
import com.gxa.cddx.www.forum.vo.PageVO;
import com.gxa.cddx.www.forum.vo.ReplyVO;
import com.gxa.cddx.www.forum.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 回复控制器
 */
@RestController
@RequestMapping("/api/reply")
public class ReplyController {
    
    @Autowired
    private ReplyService replyService;
    
    /**
     * 创建回复
     */
    @RequireAuth
    @PostMapping
    public Result<ReplyVO> createReply(@RequestBody ReplyDTO replyDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        ReplyVO replyVO = replyService.createReply(replyDTO, userId);
        return Result.success("回复成功", replyVO);
    }
    
    /**
     * 根据帖子ID分页查询回复
     */
    @GetMapping("/post/{postId}")
    public Result<PageVO<ReplyVO>> getReplyListByPost(
            @PathVariable Long postId,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        PageVO<ReplyVO> pageVO = replyService.getReplyListByPost(postId, pageNum, pageSize);
        return Result.success(pageVO);
    }
    
    /**
     * 删除回复
     */
    @RequireAuth
    @DeleteMapping("/{replyId}")
    public Result<Void> deleteReply(@PathVariable Long replyId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        replyService.deleteReply(replyId, userId);
        return Result.success("删除成功", null);
    }
}

