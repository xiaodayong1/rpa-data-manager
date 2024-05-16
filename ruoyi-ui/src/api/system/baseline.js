import request from '@/utils/request'

// 查询明细账号基线列表
export function listBaseline(query) {
  return request({
    url: '/baseline/list',
    method: 'get',
    params: query
  })
}

// 查询明细账号基线详细
export function getBaseline(id) {
  return request({
    url: '/system/baseline/' + id,
    method: 'get'
  })
}

// 新增明细账号基线
export function addBaseline(data) {
  return request({
    url: '/system/baseline',
    method: 'post',
    data: data
  })
}

// 修改明细账号基线
export function updateBaseline(data) {
  return request({
    url: '/system/baseline',
    method: 'put',
    data: data
  })
}

// 删除明细账号基线
export function delBaseline(id) {
  return request({
    url: '/baseline/' + id,
    method: 'delete'
  })
}
