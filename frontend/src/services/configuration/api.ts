import request from '@config/request';
import { system } from '@/services/api';

/** 获取系统配置列表 */
export async function queryConfigurationList(params?: any, options?: Record<string, any>) {
  return request<API.DataList<API.Configuration>>(system.configuration, {
    method: 'GET',
    params,
    ...(options || {}),
  });
}

/** 获取系统配置列表 */
export async function queryConfiguration(id: number, options?: Record<string, any>) {
  return request<API.Data<API.Configuration>>(`${system.configuration}/${id}`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 添加系统配置处理 */
export async function createConfiguration(data: API.Configuration, options?: Record<string, any>) {
  return request<API.ResponseSuccess>(system.configuration, {
    method: 'POST',
    data,
    ...(options || {}),
  });
}

/** 修改系统配置处理 */
export async function modifyConfiguration(
  id: number,
  data: API.Configuration,
  options?: Record<string, any>,
) {
  return request<API.ResponseSuccess>(`${system.configuration}/${id}`, {
    method: 'PUT',
    data,
    ...(options || {}),
  });
}

/** 删除系统配置 */
export async function removeConfiguration(id: number, options?: Record<string, any>) {
  return request<API.ResponseSuccess>(`${system.configuration}/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}

/** 批量操作系统配置 */
export async function batchConfiguration<T>(data: API.BatchRUD<T>, options?: Record<string, any>) {
  return request<API.ResponseSuccess>(system.configurationBatch, {
    method: 'POST',
    data,
    ...(options || {}),
  });
}
