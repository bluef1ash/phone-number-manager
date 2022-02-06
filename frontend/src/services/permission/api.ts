import request from "@config/request";
import { index, system } from "@/services/api";

/** 获取菜单 */
export async function queryMenuData(display: boolean, options?: Record<string, any>) {
  return request<API.ResponseMenu>(index.menu, {
    method: 'GET',
    params: {
      display,
    },
    ...(options || {}),
  });
}

/** 获取系统权限列表 */
export async function querySystemPermissionList(params?: any, options?: Record<string, any>) {
  return request<API.DataList<API.SystemPermission> & API.ResponseException>(system.permission, {
    method: 'GET',
    params,
    ...(options || {}),
  });
}

/** 获取系统权限列表表单 */
export async function querySystemPermissionSelectList(options?: Record<string, any>) {
  return request<API.Data<API.SelectList[]> & API.ResponseException>(system.permissionSelectList, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 获取系统权限列表 */
export async function querySystemPermission(id: number, options?: Record<string, any>) {
  return request<API.Data<API.SystemPermission> & API.ResponseException>(
    `${system.permission}/${id}`,
    {
      method: 'GET',
      ...(options || {}),
    },
  );
}

/** 单独操作系统权限某个属性 */
export async function patchSystemPermission(
  id: number,
  data: API.SystemPermission,
  options?: Record<string, any>,
) {
  return request<API.Data<API.SystemPermission> & API.ResponseException>(
    `${system.permission}/${id}`,
    {
      method: 'PATCH',
      data,
      ...(options || {}),
    },
  );
}

/** 添加系统权限处理 */
export async function createSystemPermission(
  data: API.SystemPermission,
  options?: Record<string, any>,
) {
  return request<API.ResponseSuccess & API.ResponseException>(system.permission, {
    method: 'POST',
    data,
    ...(options || {}),
  });
}

/** 修改系统权限处理 */
export async function modifySystemPermission(
  data: API.SystemPermission,
  options?: Record<string, any>,
) {
  return request<API.ResponseSuccess & API.ResponseException>(system.permission, {
    method: 'PUT',
    data,
    ...(options || {}),
  });
}

/** 删除系统权限 */
export async function removeSystemPermission(id: number, options?: Record<string, any>) {
  return request<API.ResponseSuccess & API.ResponseException>(`${system.permission}/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}

/** 批量操作系统权限 */
export async function batchSystemPermission<T>(
  data: API.BatchRUD<T>,
  options?: Record<string, any>,
) {
  return request<API.ResponseSuccess & API.ResponseException>(system.permissionBatch, {
    method: 'POST',
    data,
    ...(options || {}),
  });
}
