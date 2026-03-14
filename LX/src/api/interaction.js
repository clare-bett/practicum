import request from '@/utils/request'

// ===== 点赞相关 =====

/**
 * 点赞帖子
 */
export function likePost(postId) {
  return request({
    url: `/api/interaction/like/${postId}`,
    method: 'post'
  })
}

/**
 * 取消点赞
 */
export function unlikePost(postId) {
  return request({
    url: `/api/interaction/like/${postId}`,
    method: 'delete'
  })
}

/**
 * 检查是否已点赞
 */
export function checkLikeStatus(postId) {
  return request({
    url: `/api/interaction/like/check/${postId}`,
    method: 'get'
  })
}

// ===== 收藏相关 =====

/**
 * 收藏帖子
 */
export function favoritePost(postId) {
  return request({
    url: `/api/interaction/favorite/${postId}`,
    method: 'post'
  })
}

/**
 * 取消收藏
 */
export function unfavoritePost(postId) {
  return request({
    url: `/api/interaction/favorite/${postId}`,
    method: 'delete'
  })
}

/**
 * 检查是否已收藏
 */
export function checkFavoriteStatus(postId) {
  return request({
    url: `/api/interaction/favorite/check/${postId}`,
    method: 'get'
  })
}

/**
 * 分页获取当前用户收藏的帖子列表
 */
export function getMyFavorites(params = {}) {
  return request({
    url: '/api/interaction/favorites',
    method: 'get',
    params: {
      pageNum: params.pageNum ?? 1,
      pageSize: params.pageSize ?? 10
    }
  })
}

