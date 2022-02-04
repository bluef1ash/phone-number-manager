import request from '@config/request';
import { system } from '@/services/api';

/** 获取系统用户列表 */
export async function querySystemUserList(params?: any, options?: Record<string, any>) {
  return request<API.DataList<API.SystemUser> & API.ErrorResponse>(system.user, {
    method: 'GET',
    params,
    ...(options || {}),
  });
}

/** 获取系统用户列表 */
export async function querySystemUser(id: number, options?: Record<string, any>) {
  return request<API.Data<API.SystemUser> & API.ErrorResponse>(`${system.user}/${id}`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 锁定、解锁、启用、禁用系统用户 */
export async function patchSystemUser(
  id: number,
  data: API.SystemUser,
  options?: Record<string, any>,
) {
  return request<API.Data<API.SystemUser> & API.ErrorResponse>(`${system.user}/${id}`, {
    method: 'PATCH',
    data,
    ...(options || {}),
  });
}

/** 添加系统用户处理 */
export async function createSystemUser(data: API.SystemUser, options?: Record<string, any>) {
  return request<API.ResponseSuccess & API.ErrorResponse>(system.user, {
    method: 'POST',
    data,
    ...(options || {}),
  });
}

/** 修改系统用户处理 */
export async function modifySystemUser(data: API.SystemUser, options?: Record<string, any>) {
  return request<API.ResponseSuccess & API.ErrorResponse>(system.user, {
    method: 'PUT',
    data,
    ...(options || {}),
  });
}

/** 删除系统用户 */
export async function removeSystemUser(id: number, options?: Record<string, any>) {
  return request<API.ResponseSuccess & API.ErrorResponse>(`${system.user}/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}

/** 批量操作系统用户 */
export async function batchSystemUser<T>(data: API.BatchRUD<T>, options?: Record<string, any>) {
  return request<API.ResponseSuccess & API.ErrorResponse>(system.userBatch, {
    method: 'POST',
    data,
    ...(options || {}),
  });
}
