package com.gxa.cddx.www.forum.controller;

import com.gxa.cddx.www.forum.annotation.RequireAuth;
import com.gxa.cddx.www.forum.dto.ReportDTO;
import com.gxa.cddx.www.forum.service.ReportService;
import com.gxa.cddx.www.forum.vo.PageVO;
import com.gxa.cddx.www.forum.vo.ReportVO;
import com.gxa.cddx.www.forum.vo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 举报管理控制器
 */
@RestController
@RequestMapping("/api/report")
public class ReportController {
    
    private static final Logger log = LoggerFactory.getLogger(ReportController.class);
    
    @Autowired
    private ReportService reportService;
    
    /**
     * 提交举报
     */
    @PostMapping("/submit")
    @RequireAuth
    public Result<?> submitReport(@RequestBody ReportDTO reportDTO, @RequestAttribute("userId") Long userId) {
        boolean success = reportService.submitReport(
            reportDTO.getReportType(),
            reportDTO.getTargetId(),
            reportDTO.getReason(),
            userId
        );
        
        if (success) {
            return Result.success("举报成功，我们会尽快处理");
        } else {
            return Result.error("举报失败");
        }
    }
    
    /**
     * 管理员查询举报列表
     */
    @GetMapping("/list")
    @RequireAuth(admin = true)
    public Result<PageVO<ReportVO>> getReportList(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        PageVO<ReportVO> pageVO = reportService.getReportList(status, pageNum, pageSize);
        return Result.success(pageVO);
    }
    
    /**
     * 管理员处理举报
     */
    @PostMapping("/handle/{reportId}")
    @RequireAuth(admin = true)
    public Result<?> handleReport(
            @PathVariable Long reportId,
            @RequestParam String handleResult,
            @RequestParam String action,
            @RequestAttribute("userId") Long userId) {
        
        boolean success = reportService.handleReport(reportId, handleResult, userId, action);
        
        if (success) {
            return Result.success("处理成功");
        } else {
            return Result.error("处理失败");
        }
    }
    
    /**
     * 获取待处理举报数量
     */
    @GetMapping("/pending/count")
    @RequireAuth(admin = true)
    public Result<Long> getPendingReportCount() {
        long count = reportService.getPendingReportCount();
        return Result.success(count);
    }
}

