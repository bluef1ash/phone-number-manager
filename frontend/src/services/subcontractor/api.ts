import request from "@config/request";
import { subcontractor, subcontractorBatch, subcontractorSelect } from "@/services/api";

/** 获取社区分包人员列表 */
export async function querySubcontractorList(params?: any, options?: Record<string, any>) {
  return request.get<API.DataList<API.Subcontractor> & API.ResponseException>(subcontractor, {
    params,
    ...(options || {}),
  });
}

/** 获取社区分包人员表单列表 */
export async function querySubcontractorSelectList(
  parentIds: number[],
  options?: Record<string, any>,
) {
  return request.get<API.Data<API.SelectList[]> & API.ResponseException>(subcontractorSelect, {
    params: {
      parentIds: parentIds.toString(),
    },
    ...(options || {}),
  });
}

/** 获取社区分包人员列表 */
export async function querySubcontractor(id: number, options?: Record<string, any>) {
  return request.get<API.Data<API.Subcontractor> & API.ResponseException>(
    `${subcontractor}/${id}`,
    {
      ...(options || {}),
    },
  );
}

/** 锁定、解锁、启用、禁用社区分包人员 */
export async function patchSubcontractor(
  id: number,
  data: API.Subcontractor,
  options?: Record<string, any>,
) {
  return request.patch<API.Data<API.Subcontractor> & API.ResponseException>(
    `${subcontractor}/${id}`,
    {
      data,
      ...(options || {}),
    },
  );
}

/** 添加社区分包人员处理 */
export async function createSubcontractor(data: API.Subcontractor, options?: Record<string, any>) {
  return request.post<API.ResponseSuccess & API.ResponseException>(subcontractor, {
    data,
    ...(options || {}),
  });
}

/** 修改社区分包人员处理 */
export async function modifySubcontractor(
  id: number,
  data: API.Subcontractor,
  options?: Record<string, any>,
) {
  return request.put<API.ResponseSuccess & API.ResponseException>(`${subcontractor}/${id}`, {
    data,
    ...(options || {}),
  });
}

/** 删除社区分包人员 */
export async function removeSubcontractor(id: number, options?: Record<string, any>) {
  return request.delete<API.ResponseSuccess & API.ResponseException>(`${subcontractor}/${id}`, {
    ...(options || {}),
  });
}

/** 批量操作社区分包人员 */
export async function batchSubcontractor(
  data: API.BatchRUD<number[]>,
  options?: Record<string, any>,
) {
  return request.post<API.ResponseSuccess & API.ResponseException>(subcontractorBatch, {
    data,
    ...(options || {}),
  });
}
