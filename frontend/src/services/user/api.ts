import request from "@config/request";
import { system } from "@/services/api";

/** 获取当前登录系统用户 */
export async function queryCurrentUser(options?: Record<string, any>) {
  return request.get<API.Data<API.SystemUser> & API.ResponseException>(system.currentUser, {
    ...(options || {}),
  });
}

/** 获取系统用户列表 */
export async function querySystemUserList(params?: any, options?: Record<string, any>) {
  return request.get<API.DataList<API.SystemUser> & API.ResponseException>(system.user, {
    params,
    ...(options || {}),
  });
}

/** 获取系统用户表单列表 */
export async function querySystemUserSelectList(
  parentIds: number[],
  options?: Record<string, any>,
) {
  return request.get<API.Data<API.SelectList[]> & API.ResponseException>(system.userSelectList, {
    params: {
      parentIds: parentIds.toString(),
    },
    ...(options || {}),
  });
}

/** 获取系统用户详细信息 */
export async function querySystemUser(id: number, options?: Record<string, any>) {
  return request.get<API.Data<API.SystemUser> & API.ResponseException>(`${system.user}/${id}`, {
    ...(options || {}),
  });
}

/** 锁定、解锁、启用、禁用系统用户 */
export async function patchSystemUser(
  id: number,
  data: API.SystemUser,
  options?: Record<string, any>,
) {
  return request.patch<API.Data<API.SystemUser> & API.ResponseException>(`${system.user}/${id}`, {
    data,
    ...(options || {}),
  });
}

/** 添加系统用户处理 */
export async function createSystemUser(data: API.SystemUser, options?: Record<string, any>) {
  return request.post<API.ResponseSuccess & API.ResponseException>(system.user, {
    data,
    ...(options || {}),
  });
}

/** 修改系统用户处理 */
export async function modifySystemUser(
  id: number,
  data: API.SystemUser,
  options?: Record<string, any>,
) {
  return request.put<API.ResponseSuccess & API.ResponseException>(`${system.user}/${id}`, {
    data,
    ...(options || {}),
  });
}

/** 删除系统用户 */
export async function removeSystemUser(id: number, options?: Record<string, any>) {
  return request.delete<API.ResponseSuccess & API.ResponseException>(`${system.user}/${id}`, {
    ...(options || {}),
  });
}

/** 批量操作系统用户 */
export async function batchSystemUser<T>(data: API.BatchRUD<T>, options?: Record<string, any>) {
  return request.post<API.ResponseSuccess & API.ResponseException>(system.userBatch, {
    data,
    ...(options || {}),
  });
}
