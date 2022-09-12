import {
  communityResident,
  communityResidentBatch,
  communityResidentDownloadExcel,
  communityResidentImportExcel,
} from '@/services/api';
import request from '@config/request';

/** 获取社区居民列表 */
export async function queryCommunityResidentList(params?: any, options?: Record<string, any>) {
  return request.get<API.DataList<API.CommunityResident> & API.ResponseException>(
    communityResident,
    {
      params,
      ...(options || {}),
    },
  );
}

/** 获取社区居民列表 */
export async function queryCommunityResident(id: number, options?: Record<string, any>) {
  return request.get<API.Data<API.CommunityResident> & API.ResponseException>(
    `${communityResident}/${id}`,
    {
      ...(options || {}),
    },
  );
}

/** 单独字段修改社区居民 */
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

/** 添加社区居民处理 */
export async function createCommunityResident(
  data: API.CommunityResident,
  options?: Record<string, any>,
) {
  return request.post<API.ResponseSuccess & API.ResponseException>(communityResident, {
    data,
    ...(options || {}),
  });
}

/** 修改社区居民处理 */
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

/** 删除社区居民 */
export async function removeCommunityResident(id: number, options?: Record<string, any>) {
  return request.delete<API.ResponseSuccess & API.ResponseException>(`${communityResident}/${id}`, {
    ...(options || {}),
  });
}

/** 批量操作社区居民 */
export async function batchCommunityResident<T>(
  data: API.BatchRUD<T[]>,
  options?: Record<string, any>,
) {
  return request.post<API.ResponseSuccess & API.ResponseException>(communityResidentBatch, {
    data,
    ...(options || {}),
  });
}

/** 上传表格 */
export async function uploadCommunityResidentExcel(
  formData: FormData,
  options?: Record<string, any>,
) {
  return request.post<API.ResponseSuccess & API.ResponseException>(communityResidentImportExcel, {
    requestType: 'form',
    data: formData,
    ...(options || {}),
  });
}

/** 下载表格 */
export async function downloadCommunityResidentExcel(options?: Record<string, any>) {
  return request.get(communityResidentDownloadExcel, {
    getResponse: true,
    responseType: 'blob',
    ...(options || {}),
  });
}
