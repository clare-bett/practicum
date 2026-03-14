import request from '@/utils/request'

/**
 * ===== 帖子管理 =====
 */

/**
 * 管理员获取所有帖子（包括已删除）
 */
export function getAdminPostList(params) {
  return request({
    url: '/api/post/admin/list',
    method: 'get',
    params
  })
}

/**
 * 管理员删除帖子
 */
export function adminDeletePost(postId) {
  return request({
    url: `/api/post/admin/${postId}`,
    method: 'delete'
  })
}

/**
 * 管理员恢复帖子
 */
export function adminRestorePost(postId) {
  return request({
    url: `/api/post/admin/${postId}/restore`,
    method: 'put'
  })
}

/**
 * ===== 板块管理 =====
 */

/**
 * 创建板块
 */
export function createCategory(data) {
  return request({
    url: '/api/category',
    method: 'post',
    data
  })
}

/**
 * 更新板块
 */
export function updateCategory(categoryId, data) {
  return request({
    url: `/api/category/${categoryId}`,
    method: 'put',
    data
  })
}

/**
 * 删除板块
 */
export function deleteCategory(categoryId) {
  return request({
    url: `/api/category/${categoryId}`,
    method: 'delete'
  })
}

/**
 * ===== 用户管理 =====
 */

/**
 * 管理员获取所有用户列表
 */
export function getAdminUserList(params) {
  return request({
    url: '/api/user/admin/list',
    method: 'get',
    params
  })
}

/**
 * 管理员冻结用户
 */
export function adminFreezeUser(userId) {
  return request({
    url: `/api/user/admin/${userId}/freeze`,
    method: 'put'
  })
}

/**
 * 管理员恢复用户
 */
export function adminUnfreezeUser(userId) {
  return request({
    url: `/api/user/admin/${userId}/unfreeze`,
    method: 'put'
  })
}

