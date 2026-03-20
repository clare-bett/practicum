package com.gxa.cddx.www.forum.service;

import com.gxa.cddx.www.forum.dto.ReplyDTO;
import com.gxa.cddx.www.forum.vo.PageVO;
import com.gxa.cddx.www.forum.vo.ReplyVO;

/**
 * 回复服务接口
 */
public interface ReplyService {
    
    /**
     * 创建回复
     */
    ReplyVO createReply(ReplyDTO replyDTO, Long userId);
    
    /**
     * 根据帖子ID分页查询回复
     */
    PageVO<ReplyVO> getReplyListByPost(Long postId, Integer pageNum, Integer pageSize);
    
    /**
     * 删除回复
     */
    void deleteReply(Long replyId, Long userId);
}

