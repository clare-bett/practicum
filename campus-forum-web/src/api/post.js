import request from '@/utils/request'

/**
 * 创建帖子
 */
export function createPost(data) {
  return request({
    url: '/api/post',
    method: 'post',
    data
  })
}

/**
 * 获取帖子详情
 */
export function getPostById(postId) {
  return request({
    url: `/api/post/${postId}`,
    method: 'get'
  })
}

/**
 * 分页查询帖子列表
 */
export function getPostList(params) {
  return request({
    url: '/api/post/list',
    method: 'get',
    params
  })
}

/**
 * 根据板块ID分页查询帖子
 */
export function getPostListByCategory(categoryId, params) {
  return request({
    url: `/api/post/category/${categoryId}`,
    method: 'get',
    params
  })
}

/**
 * 根据作者ID分页查询帖子
 */
export function getPostListByAuthor(authorId, params) {
  return request({
    url: `/api/post/author/${authorId}`,
    method: 'get',
    params
  })
}

/**
 * 更新帖子
 */
export function updatePost(postId, data) {
  return request({
    url: `/api/post/${postId}`,
    method: 'put',
    data
  })
}

/**
 * 删除帖子
 */
export function deletePost(postId) {
  return request({
    url: `/api/post/${postId}`,
    method: 'delete'
  })
}

/**
 * 搜索帖子（支持标题和内容模糊搜索）
 */
export function searchPosts(keyword, params) {
  return request({
    url: '/api/post/search',
    method: 'get',
    params: {
      keyword,
      ...params
    }
  })
}

