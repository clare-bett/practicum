import request from '@/utils/request'

/**
 * 创建回复
 */
export function createReply(data) {
  return request({
    url: '/api/reply',
    method: 'post',
    data
  })
}

/**
 * 根据帖子ID分页查询回复
 */
export function getReplyListByPost(postId, params) {
  return request({
    url: `/api/reply/post/${postId}`,
    method: 'get',
    params
  })
}

/**
 * 删除回复
 */
export function deleteReply(replyId) {
  return request({
    url: `/api/reply/${replyId}`,
    method: 'delete'
  })
}

