import request from '@/utils/request'
import dart from 'highlight.js/lib/languages/dart'

// 查询明细详情校验宽列表
export function listVerify(query) {
  return request({
    url: '/transaction/list',
    method: 'get',
    params: query
  })
}

export function listAccount() {
  return request({
    url: '/transaction/listAccount',
    method: 'get'
  })
}

export function upload(query) {
  return request({
    url: '/convert/detail/upload',
    method: 'post',
    data:query
  })
}

export function listBank() {
  return request({
    url: '/transaction/listBank',
    method: 'get'
  })
}

export function insertTransaction(query) {
  return request({
    url: '/websocket/transaction',
    method: 'post',
    data: query
  })
}

export function updateTransaction(query) {
  return request({
    url: '/transaction/updateTransactionVerify',
    method: 'post',
    data: query
  })
}

export function getVerifyByid(id) {
  return request({
    url: '/transaction/getVerifyByid/' + id,
    method: 'get'
  })
}

export function setBaseLine(params) {
  return request({
    url: '/check/promoteBaseLine',
    method: 'get',
    params: params
  })
}

export function checkTask() {
  return request({
    url: '/check/checkTask',
    method: 'get'
  })
}

export function checkTransaction(param) {
  return request({
    url: '/check/checkTransaction/' + param,
    method: 'get'
  })
}

// 查询明细详情校验宽详细
export function listHistory(query) {
  return request({
    url: '/transaction/history',
    method: 'get',
    params: query
  })
}

export function addHistoryBalance(query) {
  return request({
    url: '/websocket/historyBalance',
    method: 'post',
    data: query
  })
}
