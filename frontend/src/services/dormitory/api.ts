import {
  dormitoryManager,
  dormitoryManagerBatch,
  dormitoryManagerDownloadExcel,
  dormitoryManagerImportExcel,
} from '@/services/api';
import request from '@config/request';

/** 获取社区楼长列表 */
export async function queryDormitoryManagerList(params?: any, options?: Record<string, any>) {
  return request.get<API.DataList<API.DormitoryManager> & API.ResponseException>(dormitoryManager, {
    params,
    ...(options || {}),
  });
}

/** 获取社区楼长列表 */
export async function queryDormitoryManager(id: number, options?: Record<string, any>) {
  return request.get<API.Data<API.DormitoryManager> & API.ResponseException>(
    `${dormitoryManager}/${id}`,
    {
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
  return request.patch<API.Data<API.DormitoryManager> & API.ResponseException>(
    `${dormitoryManager}/${id}`,
    {
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
  return request.post<API.ResponseSuccess & API.ResponseException>(dormitoryManager, {
    data,
    ...(options || {}),
  });
}

/** 修改社区楼长处理 */
export async function modifyDormitoryManager(
  id: number,
  data: API.DormitoryManager,
  options?: Record<string, any>,
) {
  return request.put<API.ResponseSuccess & API.ResponseException>(`${dormitoryManager}/${id}`, {
    data,
    ...(options || {}),
  });
}

/** 删除社区楼长 */
export async function removeDormitoryManager(id: number, options?: Record<string, any>) {
  return request.delete<API.ResponseSuccess & API.ResponseException>(`${dormitoryManager}/${id}`, {
    ...(options || {}),
  });
}

/** 批量操作社区楼长 */
export async function batchDormitoryManager(
  data: API.BatchRUD<number[]>,
  options?: Record<string, any>,
) {
  return request.post<API.ResponseSuccess & API.ResponseException>(dormitoryManagerBatch, {
    data,
    ...(options || {}),
  });
}

/** 上传表格 */
export async function uploadDormitoryManagerExcel(
  formData: FormData,
  options?: Record<string, any>,
) {
  return request.post<API.ResponseSuccess & API.ResponseException>(dormitoryManagerImportExcel, {
    requestType: 'form',
    data: formData,
    ...(options || {}),
  });
}

/** 下载表格 */
export async function downloadDormitoryManagerExcel(options?: Record<string, any>) {
  return request.get(dormitoryManagerDownloadExcel, {
    getResponse: true,
    responseType: 'blob',
    ...(options || {}),
  });
}
