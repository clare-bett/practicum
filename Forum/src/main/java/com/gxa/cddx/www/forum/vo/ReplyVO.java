package com.gxa.cddx.www.forum.vo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 回复VO（支持两级回复结构）
 */
public class ReplyVO {
    
    private Long id;
    private Long postId;
    private Long userId;
    private String username;
    private String userAvatar;
    private String content;
    private Long parentId;
    private Long replyToUserId;
    private String replyToUsername;
    private Integer likeCount;
    private Integer status;
    private Integer isAi;
    private LocalDateTime createTime;
    
    // 子回复列表（二级回复）
    private List<ReplyVO> children;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getPostId() {
        return postId;
    }
    
    public void setPostId(Long postId) {
        this.postId = postId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getUserAvatar() {
        return userAvatar;
    }
    
    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Long getParentId() {
        return parentId;
    }
    
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    
    public Long getReplyToUserId() {
        return replyToUserId;
    }
    
    public void setReplyToUserId(Long replyToUserId) {
        this.replyToUserId = replyToUserId;
    }
    
    public String getReplyToUsername() {
        return replyToUsername;
    }
    
    public void setReplyToUsername(String replyToUsername) {
        this.replyToUsername = replyToUsername;
    }
    
    public Integer getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public Integer getIsAi() {
        return isAi;
    }
    
    public void setIsAi(Integer isAi) {
        this.isAi = isAi;
    }
    
    public List<ReplyVO> getChildren() {
        return children;
    }
    
    public void setChildren(List<ReplyVO> children) {
        this.children = children;
    }
}
