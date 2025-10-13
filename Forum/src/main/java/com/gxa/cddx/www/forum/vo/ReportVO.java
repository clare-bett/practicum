package com.gxa.cddx.www.forum.vo;

import java.time.LocalDateTime;

/**
 * 举报VO
 */
public class ReportVO {
    
    private Long id;
    
    // 举报类型：1-帖子，2-回复
    private Integer reportType;
    
    private String reportTypeName;
    
    // 被举报内容ID
    private Long targetId;
    
    // 被举报内容摘要
    private String targetContent;
    
    // 举报人
    private Long reporterId;
    private String reporterName;
    
    // 举报原因
    private String reason;
    
    // 处理状态：0-待处理，1-已处理，2-已驳回
    private Integer status;
    
    private String statusName;
    
    // 处理结果
    private String handleResult;
    
    // 处理人
    private Long handlerId;
    private String handlerName;
    
    // 处理时间
    private LocalDateTime handleTime;
    
    private LocalDateTime createTime;
    
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
    
    public String getReportTypeName() {
        return reportTypeName;
    }
    
    public void setReportTypeName(String reportTypeName) {
        this.reportTypeName = reportTypeName;
    }
    
    public Long getTargetId() {
        return targetId;
    }
    
    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }
    
    public String getTargetContent() {
        return targetContent;
    }
    
    public void setTargetContent(String targetContent) {
        this.targetContent = targetContent;
    }
    
    public Long getReporterId() {
        return reporterId;
    }
    
    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }
    
    public String getReporterName() {
        return reporterName;
    }
    
    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
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
    
    public String getStatusName() {
        return statusName;
    }
    
    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
    
    public String getHandleResult() {
        return handleResult;
    }
    
    public void setHandleResult(String handleResult) {
        this.handleResult = handleResult;
    }
    
    public Long getHandlerId() {
        return handlerId;
    }
    
    public void setHandlerId(Long handlerId) {
        this.handlerId = handlerId;
    }
    
    public String getHandlerName() {
        return handlerName;
    }
    
    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
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
}

