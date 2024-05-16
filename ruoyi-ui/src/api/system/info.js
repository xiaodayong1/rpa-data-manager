import request from '@/utils/request'

// 查询【请填写功能名称】列表
export function listInfo(query) {
  return request({
    url: '/accountInfo/list',
    method: 'get',
    params: query
  })
}

export function openDevice(cabinetCode,usbPort) {
  return request({
    url: '/websocket/openDevice1/' + cabinetCode + '/' + usbPort,
    method: 'get'
  })
}

export function closeDevice(cabinetCode, usbPort) {
  return request({
    url: '/websocket/closeDevice1/' + cabinetCode + '/' + usbPort,
    method: 'get'
  })
}

// 查询【请填写功能名称】详细
export function getInfo(id) {
  return request({
    url: '/accountInfo/' + id,
    method: 'get'
  })
}

// 新增【请填写功能名称】
export function addInfo(data) {
  return request({
    url: '/accountInfo/add',
    method: 'post',
    data: data
  })
}

// 修改【请填写功能名称】
export function updateInfo(data) {
  return request({
    url: '/accountInfo/edit',
    method: 'post',
    data: data
  })
}

// 删除【请填写功能名称】
export function delInfo(id) {
  return request({
    url: '/accountInfo/' + id,
    method: 'delete'
  })
}
