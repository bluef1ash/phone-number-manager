import request from "@config/request";
import { company, companyBatch, companySelect } from "@/services/api";

/** 获取单位列表 */
export async function queryCompanyList(params?: any, options?: Record<string, any>) {
  return request<API.DataList<API.Company> & API.ResponseException>(company, {
    method: 'GET',
    params,
    ...(options || {}),
  });
}

/** 获取单位表单列表 */
export async function queryCompanySelectList(options?: Record<string, any>) {
  return request<API.Data<API.SelectList[]> & API.ResponseException>(companySelect, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 获取单位列表 */
export async function queryCompany(id: number, options?: Record<string, any>) {
  return request<API.Data<API.Company> & API.ResponseException>(`${company}/${id}`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 锁定、解锁、启用、禁用单位 */
export async function patchCompany(id: number, data: API.Company, options?: Record<string, any>) {
  return request<API.Data<API.Company> & API.ResponseException>(`${company}/${id}`, {
    method: 'PATCH',
    data,
    ...(options || {}),
  });
}

/** 添加单位处理 */
export async function createCompany(data: API.Company, options?: Record<string, any>) {
  return request<API.ResponseSuccess & API.ResponseException>(company, {
    method: 'POST',
    data,
    ...(options || {}),
  });
}

/** 修改单位处理 */
export async function modifyCompany(data: API.Company, options?: Record<string, any>) {
  return request<API.ResponseSuccess & API.ResponseException>(company, {
    method: 'PUT',
    data,
    ...(options || {}),
  });
}

/** 删除单位 */
export async function removeCompany(id: number, options?: Record<string, any>) {
  return request<API.ResponseSuccess & API.ResponseException>(`${company}/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}

/** 批量操作单位 */
export async function batchCompany(data: API.BatchRUD<number[]>, options?: Record<string, any>) {
  return request<API.ResponseSuccess & API.ResponseException>(companyBatch, {
    method: 'POST',
    data,
    ...(options || {}),
  });
}
