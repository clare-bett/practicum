import request from '@/utils/request'

export function getKnowledgeList() {
  return request({
    url: '/api/knowledge/list',
    method: 'get'
  })
}

export function addKnowledge(data) {
  return request({
    url: '/api/knowledge',
    method: 'post',
    data
  })
}

export function deleteKnowledge(id) {
  return request({
    url: `/api/knowledge/${id}`,
    method: 'delete'
  })
}
