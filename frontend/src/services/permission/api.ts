import { index, system } from '@/services/api';
import request from '@config/request';

/** 获取菜单 */
export async function queryMenuData(options?: Record<string, any>) {
  return request.get<API.ResponseMenu & API.ResponseException>(index.menu, {
    ...(options || {}),
  });
}

/** 获取系统权限列表 */
export async function querySystemPermissionList(params?: any, options?: Record<string, any>) {
  return request.get<API.DataList<API.SystemPermission> & API.ResponseException>(
    system.permission,
    {
      params,
      ...(options || {}),
    },
  );
}

/** 获取系统权限列表表单 */
export async function querySystemPermissionSelectList(
  parentIds: number[],
  options?: Record<string, any>,
) {
  return request.get<API.Data<API.SelectList[]> & API.ResponseException>(
    system.permissionSelectList,
    {
      params: {
        parentIds,
      },
      ...(options || {}),
    },
  );
}

/** 获取系统权限列表 */
export async function querySystemPermission(id: number, options?: Record<string, any>) {
  return request.get<API.Data<API.SystemPermission> & API.ResponseException>(
    `${system.permission}/${id}`,
    {
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
  return request.patch<API.Data<API.SystemPermission> & API.ResponseException>(
    `${system.permission}/${id}`,
    {
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
  return request.post<API.ResponseSuccess & API.ResponseException>(system.permission, {
    data,
    ...(options || {}),
  });
}

/** 修改系统权限处理 */
export async function modifySystemPermission(
  id: number,
  data: API.SystemPermission,
  options?: Record<string, any>,
) {
  return request.put<API.ResponseSuccess & API.ResponseException>(`${system.permission}/${id}`, {
    data,
    ...(options || {}),
  });
}

/** 删除系统权限 */
export async function removeSystemPermission(id: number, options?: Record<string, any>) {
  return request.delete<API.ResponseSuccess & API.ResponseException>(`${system.permission}/${id}`, {
    ...(options || {}),
  });
}

/** 批量操作系统权限 */
export async function batchSystemPermission<T>(
  data: API.BatchRUD<T>,
  options?: Record<string, any>,
) {
  return request.post<API.ResponseSuccess & API.ResponseException>(system.permissionBatch, {
    data,
    ...(options || {}),
  });
}
