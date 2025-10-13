package com.gxa.cddx.www.forum.service.impl;

import com.gxa.cddx.www.forum.constant.CommonConstant;
import com.gxa.cddx.www.forum.entity.*;
import com.gxa.cddx.www.forum.exception.BusinessException;
import com.gxa.cddx.www.forum.mapper.*;
import com.gxa.cddx.www.forum.service.ReportService;
import com.gxa.cddx.www.forum.vo.PageVO;
import com.gxa.cddx.www.forum.vo.ReportVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 举报服务实现
 */
@Service
public class ReportServiceImpl implements ReportService {
    
    private static final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);
    
    @Autowired
    private ReportRepository reportRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private ReplyRepository replyRepository;
    
    @Override
    @Transactional
    public boolean submitReport(Integer reportType, Long targetId, String reason, Long reporterId) {
        // 检查举报人是否存在
        User reporter = userRepository.findById(reporterId)
            .orElseThrow(() -> new BusinessException("用户不存在"));
        
        // 检查被举报内容是否存在
        if (reportType == 1) {
            // 帖子
            postRepository.findById(targetId)
                .orElseThrow(() -> new BusinessException("帖子不存在"));
        } else if (reportType == 2) {
            // 回复
            replyRepository.findById(targetId)
                .orElseThrow(() -> new BusinessException("回复不存在"));
        } else {
            throw new BusinessException("举报类型错误");
        }
        
        // 创建举报记录
        Report report = new Report();
        report.setReportType(reportType);
        report.setTargetId(targetId);
        report.setReason(reason);
        report.setReporter(reporter);
        report.setStatus(0); // 待处理
        
        reportRepository.save(report);
        
        log.info("✅ 用户 {} 提交举报成功，类型: {}, 目标ID: {}", reporterId, reportType, targetId);
        return true;
    }
    
    @Override
    @Transactional
    public boolean handleReport(Long reportId, String handleResult, Long handlerId, String action) {
        // 查询举报记录
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new BusinessException("举报记录不存在"));
        
        // 检查处理人是否存在
        User handler = userRepository.findById(handlerId)
            .orElseThrow(() -> new BusinessException("用户不存在"));
        
        // 更新举报记录
        report.setHandler(handler);
        report.setHandleResult(handleResult);
        report.setHandleTime(LocalDateTime.now());
        
        // 执行处理动作
        if ("delete".equals(action)) {
            // 删除内容
            if (report.getReportType() == 1) {
                // 删除帖子
                Post post = postRepository.findById(report.getTargetId()).orElse(null);
                if (post != null) {
                    post.setStatus(CommonConstant.POST_DELETED);
                    postRepository.save(post);
                    log.info("📍 删除违规帖子: {}", report.getTargetId());
                }
            } else if (report.getReportType() == 2) {
                // 删除回复
                Reply reply = replyRepository.findById(report.getTargetId()).orElse(null);
                if (reply != null) {
                    reply.setStatus(0);
                    replyRepository.save(reply);
                    log.info("📍 删除违规回复: {}", report.getTargetId());
                }
            }
            report.setStatus(1); // 已处理
        } else if ("reject".equals(action)) {
            // 驳回举报
            report.setStatus(2); // 已驳回
        } else if ("ban".equals(action)) {
            // 禁言用户
            Long authorId = null;
            if (report.getReportType() == 1) {
                Post post = postRepository.findById(report.getTargetId()).orElse(null);
                if (post != null) {
                    authorId = post.getAuthor().getId();
                }
            } else if (report.getReportType() == 2) {
                Reply reply = replyRepository.findById(report.getTargetId()).orElse(null);
                if (reply != null) {
                    authorId = reply.getUser().getId();
                }
            }
            
            if (authorId != null) {
                User author = userRepository.findById(authorId).orElse(null);
                if (author != null) {
                    author.setStatus(CommonConstant.STATUS_LOCKED);
                    userRepository.save(author);
                    log.info("📍 禁言用户: {}", authorId);
                }
            }
            report.setStatus(1); // 已处理
        } else {
            throw new BusinessException("处理动作错误");
        }
        
        reportRepository.save(report);
        
        log.info("✅ 管理员 {} 处理举报 {} 成功，动作: {}", handlerId, reportId, action);
        return true;
    }
    
    @Override
    public PageVO<ReportVO> getReportList(Integer status, Integer pageNum, Integer pageSize) {
        // 创建分页对象
        Pageable pageable = PageRequest.of(
            pageNum - 1, 
            pageSize,
            Sort.by(Sort.Direction.DESC, "createTime")
        );
        
        // 查询
        Page<Report> reportPage;
        if (status == null) {
            reportPage = reportRepository.findAll(pageable);
        } else {
            reportPage = reportRepository.findByStatus(status, pageable);
        }
        
        // 转换为VO
        List<ReportVO> voList = reportPage.getContent().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        return new PageVO<>(
            voList,
            reportPage.getTotalElements(),
            pageNum,
            pageSize
        );
    }
    
    @Override
    public long getPendingReportCount() {
        return reportRepository.countByStatus(0);
    }
    
    /**
     * 转换为VO
     */
    private ReportVO convertToVO(Report report) {
        ReportVO vo = new ReportVO();
        vo.setId(report.getId());
        vo.setReportType(report.getReportType());
        vo.setReportTypeName(report.getReportType() == 1 ? "帖子" : "回复");
        vo.setTargetId(report.getTargetId());
        vo.setReason(report.getReason());
        vo.setStatus(report.getStatus());
        
        // 状态名称
        if (report.getStatus() == 0) {
            vo.setStatusName("待处理");
        } else if (report.getStatus() == 1) {
            vo.setStatusName("已处理");
        } else {
            vo.setStatusName("已驳回");
        }
        
        // 举报人信息
        if (report.getReporter() != null) {
            vo.setReporterId(report.getReporter().getId());
            vo.setReporterName(report.getReporter().getNickname());
        }
        
        // 处理人信息
        if (report.getHandler() != null) {
            vo.setHandlerId(report.getHandler().getId());
            vo.setHandlerName(report.getHandler().getNickname());
        }
        
        vo.setHandleResult(report.getHandleResult());
        vo.setHandleTime(report.getHandleTime());
        vo.setCreateTime(report.getCreateTime());
        
        // 获取被举报内容摘要
        String targetContent = "";
        if (report.getReportType() == 1) {
            Post post = postRepository.findById(report.getTargetId()).orElse(null);
            if (post != null) {
                targetContent = post.getTitle();
                if (targetContent.length() > 50) {
                    targetContent = targetContent.substring(0, 50) + "...";
                }
            }
        } else if (report.getReportType() == 2) {
            Reply reply = replyRepository.findById(report.getTargetId()).orElse(null);
            if (reply != null) {
                targetContent = reply.getContent();
                if (targetContent.length() > 50) {
                    targetContent = targetContent.substring(0, 50) + "...";
                }
            }
        }
        vo.setTargetContent(targetContent);
        
        return vo;
    }
}

