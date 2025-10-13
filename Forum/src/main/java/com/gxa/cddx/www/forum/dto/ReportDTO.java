package com.gxa.cddx.www.forum.dto;

/**
 * 举报DTO
 */
public class ReportDTO {
    
    // 举报类型：1-帖子，2-回复
    private Integer reportType;
    
    // 被举报内容ID
    private Long targetId;
    
    // 举报原因
    private String reason;
    
    // Getters and Setters
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
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}

