import request from '@config/request';
import { account } from '@/services/api';
import type { ExtendOptionsInit } from 'umi-request';

/** 退出登录接口 POST /api/login/outLogin */
export async function logout(options?: Record<string, any>) {
  return request<Record<string, any>>(account.logout, {
    method: 'POST',
    ...(options || {}),
  });
}

/** 登录接口 POST */
export async function login(
  body: API.LoginParams & ExtendOptionsInit,
  options?: Record<string, any>,
) {
  return request<API.LoginResult>(account.login, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
