package com.gxa.cddx.www.forum.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 举报实体类
 */
@Entity
@Table(name = "report")
public class Report {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 举报类型：1-帖子，2-回复
    @Column(nullable = false)
    private Integer reportType;
    
    // 被举报内容ID（帖子ID或回复ID）
    @Column(nullable = false)
    private Long targetId;
    
    // 举报人
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;
    
    // 举报原因
    @Column(nullable = false, length = 500)
    private String reason;
    
    // 处理状态：0-待处理，1-已处理，2-已驳回
    @Column(nullable = false)
    private Integer status = 0;
    
    // 处理结果
    @Column(length = 500)
    private String handleResult;
    
    // 处理人
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "handler_id")
    private User handler;
    
    // 处理时间
    private LocalDateTime handleTime;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateTime;
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getReportType() {
        return reportType;
    }
    
    public void setReportType(Integer reportType) {
        this.reportType = reportType;
    }
    
    public Long getTargetId() {
        return targetId;
    }
    
    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }
    
    public User getReporter() {
        return reporter;
    }
    
    public void setReporter(User reporter) {
        this.reporter = reporter;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public String getHandleResult() {
        return handleResult;
    }
    
    public void setHandleResult(String handleResult) {
        this.handleResult = handleResult;
    }
    
    public User getHandler() {
        return handler;
    }
    
    public void setHandler(User handler) {
        this.handler = handler;
    }
    
    public LocalDateTime getHandleTime() {
        return handleTime;
    }
    
    public void setHandleTime(LocalDateTime handleTime) {
        this.handleTime = handleTime;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}

