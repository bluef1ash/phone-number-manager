import {
  dormitoryManager,
  dormitoryManagerBatch,
  dormitoryManagerDownloadExcel,
  dormitoryManagerExportExcel,
  dormitoryManagerImportExcel,
} from '@/services/api';
import request from '@config/request';

/**
 * 获取社区楼长列表
 * @param params
 * @param options
 */
export async function queryDormitoryManagerList(params?: any, options?: Record<string, any>) {
  return request.get<API.DataList<API.DormitoryManager> & API.ResponseException>(dormitoryManager, {
    params,
    ...(options || {}),
  });
}

/**
 * 获取社区楼长列表
 * @param id
 * @param options
 */
export async function queryDormitoryManager(id: number, options?: Record<string, any>) {
  return request.get<API.Data<API.DormitoryManager> & API.ResponseException>(
    `${dormitoryManager}/${id}`,
    {
      ...(options || {}),
    },
  );
}

/**
 * 单独字段修改社区楼长
 * @param id
 * @param data
 * @param options
 */
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

/**
 * 添加社区楼长处理
 * @param data
 * @param options
 */
export async function createDormitoryManager(
  data: API.DormitoryManager,
  options?: Record<string, any>,
) {
  return request.post<API.ResponseSuccess & API.ResponseException>(dormitoryManager, {
    data,
    ...(options || {}),
  });
}

/**
 * 修改社区楼长处理
 * @param id
 * @param data
 * @param options
 */
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

/**
 * 删除社区楼长
 * @param id
 * @param options
 */
export async function removeDormitoryManager(id: number, options?: Record<string, any>) {
  return request.delete<API.ResponseSuccess & API.ResponseException>(`${dormitoryManager}/${id}`, {
    ...(options || {}),
  });
}

/**
 * 批量操作社区楼长
 * @param data
 * @param options
 */
export async function batchDormitoryManager<T>(
  data: API.BatchRUD<T[]>,
  options?: Record<string, any>,
) {
  return request.post<API.ResponseSuccess & API.ResponseException>(dormitoryManagerBatch, {
    data,
    ...(options || {}),
  });
}

/**
 * 上传表格
 * @param data
 * @param importId
 * @param optionsObject
 */
export async function uploadDormitoryManagerExcel(
  data?: FormData,
  importId?: number,
  optionsObject?: Record<string, any>,
) {
  let options = optionsObject || {};
  if (typeof data !== 'undefined') {
    options = { ...optionsObject, requestType: 'form', data };
  }
  if (typeof importId !== 'undefined') {
    options = { ...options, params: { importId } };
  }
  return request.post<API.ImportFileProgress & API.ResponseException>(
    dormitoryManagerImportExcel,
    options,
  );
}

/**
 * 生成 Excel 文件
 * @param exportId
 * @param options
 */
export async function exportDormitoryManagerExcel(
  exportId?: number,
  options?: Record<string, any>,
) {
  return request.get<API.ExportFileProgress & API.ResponseException>(dormitoryManagerExportExcel, {
    params: {
      exportId,
    },
    ...(options || {}),
  });
}

/**
 * 下载表格
 * @param exportId
 * @param options
 */
export async function downloadDormitoryManagerExcel(
  exportId: number,
  options?: Record<string, any>,
) {
  return request.get(dormitoryManagerDownloadExcel, {
    params: {
      exportId,
    },
    getResponse: true,
    responseType: 'blob',
    ...(options || {}),
  });
}
