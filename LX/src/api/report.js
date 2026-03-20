import request from '@/utils/request'

/**
 * 提交举报
 */
export function submitReport(data) {
  return request({
    url: '/api/report/submit',
    method: 'post',
    data
  })
}

/**
 * 管理员查询举报列表
 */
export function getReportList(params) {
  return request({
    url: '/api/report/list',
    method: 'get',
    params
  })
}

/**
 * 管理员处理举报
 */
export function handleReport(reportId, params) {
  return request({
    url: `/api/report/handle/${reportId}`,
    method: 'post',
    params
  })
}

/**
 * 获取待处理举报数量
 */
export function getPendingReportCount() {
  return request({
    url: '/api/report/pending/count',
    method: 'get'
  })
}

