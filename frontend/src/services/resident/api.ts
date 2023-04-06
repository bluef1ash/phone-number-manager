import {
  communityResident,
  communityResidentBatch,
  communityResidentDownloadExcel,
  communityResidentExportExcel,
  communityResidentImportExcel,
} from '@/services/api';
import request from '@config/request';

/**
 * 获取社区居民列表
 * @param params
 * @param options
 */
export async function queryCommunityResidentList(params?: any, options?: Record<string, any>) {
  return request.get<API.DataList<API.CommunityResident> & API.ResponseException>(
    communityResident,
    {
      params,
      ...(options || {}),
    },
  );
}

/**
 * 获取社区居民列表
 * @param id
 * @param options
 */
export async function queryCommunityResident(id: number, options?: Record<string, any>) {
  return request.get<API.Data<API.CommunityResident> & API.ResponseException>(
    `${communityResident}/${id}`,
    {
      ...(options || {}),
    },
  );
}

/**
 * 单独字段修改社区居民
 * @param id
 * @param data
 * @param options
 */
export async function patchCommunityResident(
  id: number,
  data: API.CommunityResident,
  options?: Record<string, any>,
) {
  return request.patch<API.Data<API.CommunityResident> & API.ResponseException>(
    `${communityResident}/${id}`,
    {
      data,
      ...(options || {}),
    },
  );
}

/**
 * 添加社区居民处理
 * @param data
 * @param options
 */
export async function createCommunityResident(
  data: API.CommunityResident,
  options?: Record<string, any>,
) {
  return request.post<API.ResponseSuccess & API.ResponseException>(communityResident, {
    data,
    ...(options || {}),
  });
}

/**
 * 修改社区居民处理
 * @param id
 * @param data
 * @param options
 */
export async function modifyCommunityResident(
  id: number,
  data: API.CommunityResident,
  options?: Record<string, any>,
) {
  return request.put<API.ResponseSuccess & API.ResponseException>(`${communityResident}/${id}`, {
    data,
    ...(options || {}),
  });
}

/**
 * 删除社区居民
 * @param id
 * @param options
 */
export async function removeCommunityResident(id: number, options?: Record<string, any>) {
  return request.delete<API.ResponseSuccess & API.ResponseException>(`${communityResident}/${id}`, {
    ...(options || {}),
  });
}

/**
 * 批量操作社区居民
 * @param data
 * @param options
 */
export async function batchCommunityResident<T>(
  data: API.BatchRUD<T[]>,
  options?: Record<string, any>,
) {
  return request.post<API.ResponseSuccess & API.ResponseException>(communityResidentBatch, {
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
export async function uploadCommunityResidentExcel(
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
    communityResidentImportExcel,
    options,
  );
}

/**
 * 生成 Excel 文件
 * @param exportId
 * @param options
 */
export async function exportCommunityResidentExcel(
  exportId?: number,
  options?: Record<string, any>,
) {
  return request.get<API.ExportFileProgress & API.ResponseException>(communityResidentExportExcel, {
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
export async function downloadCommunityResidentExcel(
  exportId: number,
  options?: Record<string, any>,
) {
  return request.get(communityResidentDownloadExcel, {
    params: {
      exportId,
    },
    getResponse: true,
    responseType: 'blob',
    ...(options || {}),
  });
}
