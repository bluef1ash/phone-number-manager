import request from "@config/request";
import { account } from "@/services/api";

/** 退出登录接口 */
export async function logout(id: string, options?: Record<string, any>) {
  return request.post<API.ResponseSuccess & API.ResponseException>(account.logout, {
    params: {
      id,
    },
    ...(options || {}),
  });
}

/** 登录接口 POST */
export async function login(data: API.LoginParams, options?: Record<string, any>) {
  return request.post<API.LoginResult & API.ResponseException>(account.login, {
    data,
    ...(options || {}),
  });
}
