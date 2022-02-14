import request from "@config/request";
import { dormitoryManager, dormitoryManagerBatch } from "@/services/api";

/** 获取社区楼长列表 */
export async function queryDormitoryManagerList(params?: any, options?: Record<string, any>) {
  return request<API.DataList<API.DormitoryManager> & API.ResponseException>(dormitoryManager, {
    method: 'GET',
    params,
    ...(options || {}),
  });
}

/** 获取社区楼长列表 */
export async function queryDormitoryManager(id: number, options?: Record<string, any>) {
  return request<API.Data<API.DormitoryManager> & API.ResponseException>(
    `${dormitoryManager}/${id}`,
    {
      method: 'GET',
      ...(options || {}),
    },
  );
}

/** 单独字段修改社区楼长 */
export async function patchDormitoryManager(
  id: number,
  data: API.DormitoryManager,
  options?: Record<string, any>,
) {
  return request<API.Data<API.DormitoryManager> & API.ResponseException>(
    `${dormitoryManager}/${id}`,
    {
      method: 'PATCH',
      data,
      ...(options || {}),
    },
  );
}

/** 添加社区楼长处理 */
export async function createDormitoryManager(
  data: API.DormitoryManager,
  options?: Record<string, any>,
) {
  return request<API.ResponseSuccess & API.ResponseException>(dormitoryManager, {
    method: 'POST',
    data,
    ...(options || {}),
  });
}

/** 修改社区楼长处理 */
export async function modifyDormitoryManager(
  data: API.DormitoryManager,
  options?: Record<string, any>,
) {
  return request<API.ResponseSuccess & API.ResponseException>(dormitoryManager, {
    method: 'PUT',
    data,
    ...(options || {}),
  });
}

/** 删除社区楼长 */
export async function removeDormitoryManager(id: number, options?: Record<string, any>) {
  return request<API.ResponseSuccess & API.ResponseException>(`${dormitoryManager}/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}

/** 批量操作社区楼长 */
export async function batchDormitoryManager(
  data: API.BatchRUD<number[]>,
  options?: Record<string, any>,
) {
  return request<API.ResponseSuccess & API.ResponseException>(dormitoryManagerBatch, {
    method: 'POST',
    data,
    ...(options || {}),
  });
}
