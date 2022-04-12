import request from "@config/request";
import { company, companyBatch, companySelect } from "@/services/api";

/** 获取单位列表 */
export async function queryCompanyList(params?: any, options?: Record<string, any>) {
  return request.get<API.DataList<API.Company> & API.ResponseException>(company, {
    params,
    ...(options || {}),
  });
}

/** 获取单位表单列表 */
export async function queryCompanySelectList(parentIds: number[], options?: Record<string, any>) {
  return request.get<API.Data<API.SelectList[]> & API.ResponseException>(companySelect, {
    params: {
      parentIds: parentIds.toString(),
    },
    ...(options || {}),
  });
}

/** 获取单位列表 */
export async function queryCompany(id: number, options?: Record<string, any>) {
  return request.get<API.Data<API.Company> & API.ResponseException>(`${company}/${id}`, {
    ...(options || {}),
  });
}

/** 锁定、解锁、启用、禁用单位 */
export async function patchCompany(id: number, data: API.Company, options?: Record<string, any>) {
  return request.patch<API.Data<API.Company> & API.ResponseException>(`${company}/${id}`, {
    data,
    ...(options || {}),
  });
}

/** 添加单位处理 */
export async function createCompany(data: API.Company, options?: Record<string, any>) {
  return request.post<API.ResponseSuccess & API.ResponseException>(company, {
    data,
    ...(options || {}),
  });
}

/** 修改单位处理 */
export async function modifyCompany(id: number, data: API.Company, options?: Record<string, any>) {
  return request.put<API.ResponseSuccess & API.ResponseException>(`${company}/${id}`, {
    data,
    ...(options || {}),
  });
}

/** 删除单位 */
export async function removeCompany(id: number, options?: Record<string, any>) {
  return request.delete<API.ResponseSuccess & API.ResponseException>(`${company}/${id}`, {
    ...(options || {}),
  });
}

/** 批量操作单位 */
export async function batchCompany(data: API.BatchRUD<number[]>, options?: Record<string, any>) {
  return request.post<API.ResponseSuccess & API.ResponseException>(companyBatch, {
    data,
    ...(options || {}),
  });
}
