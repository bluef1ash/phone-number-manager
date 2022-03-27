import request from "@config/request";
import { system } from "@/services/api";

/** 获取系统配置列表 */
export async function queryConfigurationList(params?: any, options?: Record<string, any>) {
  return request.get<API.DataList<API.Configuration>>(system.configuration, {
    params,
    ...(options || {}),
  });
}

/** 获取系统配置列表 */
export async function queryConfiguration(id: number, options?: Record<string, any>) {
  return request.get<API.Data<API.Configuration>>(`${system.configuration}/${id}`, {
    ...(options || {}),
  });
}

/** 添加系统配置处理 */
export async function createConfiguration(data: API.Configuration, options?: Record<string, any>) {
  return request.post<API.ResponseSuccess>(system.configuration, {
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
  return request.put<API.ResponseSuccess>(`${system.configuration}/${id}`, {
    data,
    ...(options || {}),
  });
}

/** 删除系统配置 */
export async function removeConfiguration(id: number, options?: Record<string, any>) {
  return request.delete<API.ResponseSuccess>(`${system.configuration}/${id}`, {
    ...(options || {}),
  });
}

/** 批量操作系统配置 */
export async function batchConfiguration<T>(data: API.BatchRUD<T>, options?: Record<string, any>) {
  return request.post<API.ResponseSuccess>(system.configurationBatch, {
    data,
    ...(options || {}),
  });
}
