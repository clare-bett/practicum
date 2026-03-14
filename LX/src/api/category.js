import request from '@/utils/request'

/**
 * 获取所有板块
 */
export function getCategoryList() {
  return request({
    url: '/api/category/list',
    method: 'get'
  })
}

/**
 * 获取板块详情
 */
export function getCategoryById(categoryId) {
  return request({
    url: `/api/category/${categoryId}`,
    method: 'get'
  })
}

/**
 * 创建板块（管理员）
 */
export function createCategory(data) {
  return request({
    url: '/api/category',
    method: 'post',
    data
  })
}

/**
 * 更新板块（管理员）
 */
export function updateCategory(categoryId, data) {
  return request({
    url: `/api/category/${categoryId}`,
    method: 'put',
    data
  })
}

/**
 * 删除板块（管理员）
 */
export function deleteCategory(categoryId) {
  return request({
    url: `/api/category/${categoryId}`,
    method: 'delete'
  })
}

/**
 * 恢复已删除的板块（管理员）
 */
export function restoreCategory(categoryId) {
  return request({
    url: `/api/category/${categoryId}/restore`,
    method: 'put'
  })
}

