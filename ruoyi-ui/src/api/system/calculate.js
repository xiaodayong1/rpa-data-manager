import request from '@/utils/request'

// 查询【请填写功能名称】列表
export function listCalculate(query) {
  return request({
    url: '/calculate/list',
    method: 'get',
    params: query
  })
}

// 查询【请填写功能名称】详细
export function getCalculate(id) {
  return request({
    url: '/system/calculate/' + id,
    method: 'get'
  })
}

// 新增【请填写功能名称】
export function addCalculate(data) {
  return request({
    url: '/system/calculate',
    method: 'post',
    data: data
  })
}

// 修改【请填写功能名称】
export function updateCalculate(data) {
  return request({
    url: '/system/calculate',
    method: 'put',
    data: data
  })
}

// 删除【请填写功能名称】
export function delCalculate(id) {
  return request({
    url: '/system/calculate/' + id,
    method: 'delete'
  })
}
