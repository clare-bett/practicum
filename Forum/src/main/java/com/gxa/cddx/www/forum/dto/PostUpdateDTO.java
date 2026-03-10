package com.gxa.cddx.www.forum.dto;

import jakarta.validation.constraints.Size;

/**
 * 帖子更新DTO
 * 用于编辑帖子，所有字段都是可选的
 */
public class PostUpdateDTO {
    
    @Size(min = 1, max = 100, message = "标题长度必须在1-100字符之间")
    private String title;
    
    @Size(min = 1, max = 10000, message = "内容长度必须在1-10000字符之间")
    private String content;
    
    private Long categoryId;
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    /**
     * 检查是否有任何字段需要更新
     */
    public boolean hasUpdates() {
        return (title != null && !title.trim().isEmpty()) 
            || (content != null && !content.trim().isEmpty()) 
            || categoryId != null;
    }
}

