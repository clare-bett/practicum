package com.gxa.cddx.www.forum.service.impl;

import com.gxa.cddx.www.forum.constant.CommonConstant;
import com.gxa.cddx.www.forum.constant.ResultCode;
import com.gxa.cddx.www.forum.dto.ReplyDTO;
import com.gxa.cddx.www.forum.entity.Post;
import com.gxa.cddx.www.forum.entity.Reply;
import com.gxa.cddx.www.forum.entity.User;
import com.gxa.cddx.www.forum.exception.BusinessException;
import com.gxa.cddx.www.forum.mapper.PostRepository;
import com.gxa.cddx.www.forum.mapper.ReplyRepository;
import com.gxa.cddx.www.forum.mapper.UserRepository;
import com.gxa.cddx.www.forum.service.AIReplyService;
import com.gxa.cddx.www.forum.service.ReplyService;
import com.gxa.cddx.www.forum.service.SensitiveWordService;
import com.gxa.cddx.www.forum.vo.PageVO;
import com.gxa.cddx.www.forum.vo.ReplyVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 回复服务实现类
 */
@Service
public class ReplyServiceImpl implements ReplyService {
    
    private static final Logger log = LoggerFactory.getLogger(ReplyServiceImpl.class);
    
    @Autowired
    private ReplyRepository replyRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AIReplyService aiReplyService;
    
    @Autowired
    private SensitiveWordService sensitiveWordService;
    
    @Override
    @Transactional
    public ReplyVO createReply(ReplyDTO replyDTO, Long userId) {
        // 检查帖子是否存在
        Post post = postRepository.findById(replyDTO.getPostId())
            .orElseThrow(() -> new BusinessException(ResultCode.POST_NOT_FOUND, "帖子不存在"));
        
        // 检查用户是否存在
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在"));
        
        // 检查用户是否被禁言
        if (user.getStatus() == CommonConstant.STATUS_LOCKED) {
            throw new BusinessException("您的账号已被禁言，无法发布内容");
        }
        
        // 敏感词过滤
        String content = replyDTO.getContent();
        if (sensitiveWordService.containsSensitiveWord(content)) {
            log.warn("🚨 检测到回复内容包含敏感词，用户: {}, 内容: {}", userId, content);
            throw new BusinessException("回复内容包含敏感词，请修改后重试");
        }
        
        log.info("✅ 敏感词检查通过，发布回复");
        
        // 创建回复
        Reply reply = new Reply();
        reply.setPost(post);
        reply.setUser(user);
        reply.setContent(content);
        reply.setStatus(CommonConstant.STATUS_NORMAL);
        
        // 处理父回复
        if (replyDTO.getParentId() != null) {
            Reply parent = replyRepository.findById(replyDTO.getParentId())
                .orElseThrow(() -> new BusinessException(ResultCode.REPLY_NOT_FOUND, "父回复不存在"));
            reply.setParent(parent);
        }
        
        // 处理被回复用户
        if (replyDTO.getReplyToUserId() != null) {
            User replyToUser = userRepository.findById(replyDTO.getReplyToUserId())
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND, "被回复用户不存在"));
            reply.setReplyToUser(replyToUser);
        }
        
        reply = replyRepository.save(reply);
        
        // 增加帖子回复数
        postRepository.incrementReplyCount(post.getId());
        
        // 更新帖子最后回复时间
        post.setLastReplyTime(LocalDateTime.now());
        postRepository.save(post);
        
        // 检测是否@了AI助手
        if (containsAIMention(replyDTO.getContent())) {
            log.info("检测到@AI助手，准备生成AI回复，帖子ID: {}", post.getId());
            
            // 保存参数到局部变量（避免lambda中的引用问题）
            final Long savedPostId = post.getId();
            final String savedContent = replyDTO.getContent();
            final Long savedReplyId = reply.getId();
            
            // 注册事务同步回调，在事务提交后执行
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    // 异步生成AI回复
                    aiReplyService.generateAIReplyForMentionAsync(savedPostId, savedContent, savedReplyId);
                }
            });
        }
        
        return convertToVO(reply);
    }
    
    @Override
    public PageVO<ReplyVO> getReplyListByPost(Long postId, Integer pageNum, Integer pageSize) {
        pageNum = pageNum == null ? CommonConstant.DEFAULT_PAGE_NUM : pageNum;
        pageSize = pageSize == null ? CommonConstant.DEFAULT_PAGE_SIZE : pageSize;
        
        // 只查询一级回复（parent_id为null的回复）
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Reply> page = replyRepository.findByPostIdAndParentIsNullAndStatusOrderByCreateTimeAsc(
            postId, CommonConstant.STATUS_NORMAL, pageable);
        
        return convertToPageVOWithChildren(page, postId);
    }
    
    @Override
    @Transactional
    public void deleteReply(Long replyId, Long userId) {
        Reply reply = replyRepository.findById(replyId)
            .orElseThrow(() -> new BusinessException(ResultCode.REPLY_NOT_FOUND, "回复不存在"));
        
        // 检查权限
        if (!reply.getUser().getId().equals(userId)) {
            throw new BusinessException(ResultCode.PERMISSION_DENIED, "无权限删除此回复");
        }
        
        reply.setStatus(CommonConstant.STATUS_DISABLED);
        replyRepository.save(reply);
    }
    
    /**
     * 转换为VO
     */
    private ReplyVO convertToVO(Reply reply) {
        ReplyVO vo = new ReplyVO();
        vo.setId(reply.getId());
        vo.setPostId(reply.getPost().getId());
        vo.setUserId(reply.getUser().getId());
        vo.setUsername(reply.getUser().getUsername());
        vo.setUserAvatar(reply.getUser().getAvatar());
        vo.setContent(reply.getContent());
        vo.setParentId(reply.getParent() != null ? reply.getParent().getId() : null);
        vo.setReplyToUserId(reply.getReplyToUser() != null ? reply.getReplyToUser().getId() : null);
        vo.setReplyToUsername(reply.getReplyToUser() != null ? reply.getReplyToUser().getUsername() : null);
        vo.setLikeCount(reply.getLikeCount());
        vo.setStatus(reply.getStatus());
        vo.setIsAi(reply.getIsAi());
        vo.setCreateTime(reply.getCreateTime());
        return vo;
    }
    
    /**
     * 转换为分页VO（包含子回复）
     */
    private PageVO<ReplyVO> convertToPageVOWithChildren(Page<Reply> page, Long postId) {
        List<ReplyVO> records = page.getContent().stream()
            .map(reply -> {
                ReplyVO vo = convertToVO(reply);
                // 查询并添加子回复（二级回复）
                List<Reply> children = replyRepository.findByParentIdAndStatusOrderByCreateTimeAsc(
                    reply.getId(), CommonConstant.STATUS_NORMAL);
                List<ReplyVO> childrenVOs = children.stream()
                    .map(this::convertToVO)
                    .collect(Collectors.toList());
                vo.setChildren(childrenVOs);
                return vo;
            })
            .collect(Collectors.toList());
        
        return new PageVO<>(records, page.getTotalElements(), 
            page.getNumber() + 1, page.getSize());
    }
    
    /**
     * 转换为分页VO（旧方法，保留用于兼容）
     */
    private PageVO<ReplyVO> convertToPageVO(Page<Reply> page) {
        List<ReplyVO> records = page.getContent().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        return new PageVO<>(records, page.getTotalElements(), 
            page.getNumber() + 1, page.getSize());
    }
    
    /**
     * 检测内容是否@了AI助手
     */
    private boolean containsAIMention(String content) {
        if (content == null || content.isEmpty()) {
            return false;
        }
        return content.contains("@AI助手") || content.contains("@AI");
    }
}

