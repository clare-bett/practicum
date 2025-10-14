package com.gxa.cddx.www.forum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 帖子DTO
 */
public class PostDTO {
    
    @NotBlank(message = "标题不能为空")
    @Size(min = 1, max = 100, message = "标题长度必须在1-100字符之间")
    private String title;
    
    @NotBlank(message = "内容不能为空")
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
}

