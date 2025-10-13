package com.gxa.cddx.www.forum.service;

import com.gxa.cddx.www.forum.vo.PageVO;
import com.gxa.cddx.www.forum.vo.ReportVO;

/**
 * 举报服务接口
 */
public interface ReportService {
    
    /**
     * 提交举报
     * @param reportType 举报类型：1-帖子，2-回复
     * @param targetId 被举报内容ID
     * @param reason 举报原因
     * @param reporterId 举报人ID
     * @return 是否成功
     */
    boolean submitReport(Integer reportType, Long targetId, String reason, Long reporterId);
    
    /**
     * 管理员处理举报
     * @param reportId 举报ID
     * @param handleResult 处理结果
     * @param handlerId 处理人ID
     * @param action 处理动作：delete-删除内容，reject-驳回举报，ban-禁言用户
     * @return 是否成功
     */
    boolean handleReport(Long reportId, String handleResult, Long handlerId, String action);
    
    /**
     * 分页查询举报列表
     * @param status 状态：0-待处理，1-已处理，2-已驳回，null-全部
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 举报列表
     */
    PageVO<ReportVO> getReportList(Integer status, Integer pageNum, Integer pageSize);
    
    /**
     * 获取待处理举报数量
     * @return 数量
     */
    long getPendingReportCount();
}

