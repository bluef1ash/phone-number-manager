import request from "@config/request";
import { index } from "@/services/api";

/** 获取控制台数据 */
export async function queryDashboardComputed(
  data?: API.DashboardComputedPostData,
  options?: Record<string, any>,
) {
  return request.post<API.Data<API.DashboardComputed> & API.ResponseException>(
    index.dashboardComputed,
    {
      data,
      ...(options || {}),
    },
  );
}
