import request from "@config/request";
import {
  communityResidentBaseMessage,
  communityResidentChart,
  dormitoryManagerBaseMessage,
  dormitoryManagerChart,
  system
} from "@/services/api";
import type { ColumnConfig } from "@ant-design/charts";

/** 获取社区居民基础数据 */
export async function queryCommunityResidentBaseMessage(
  data?: API.DashboardComputedPostData,
  options?: Record<string, any>,
) {
  return request.post<API.Data<API.CommunityResidentComputedBaseMessage> & API.ResponseException>(
    communityResidentBaseMessage,
    {
      data,
      ...(options || {}),
    },
  );
}

/** 获取社区居民图表数据 */
export async function queryCommunityResidentChart(
  data?: API.DashboardComputedPostData,
  options?: Record<string, any>,
) {
  return request.post<API.Data<ColumnConfig> & API.ResponseException>(communityResidentChart, {
    data,
    ...(options || {}),
  });
}

/** 获取社区居民楼片长基础数据 */
export async function queryDormitoryManagerBaseMessage(
  data?: API.DashboardComputedPostData,
  options?: Record<string, any>,
) {
  return request.post<API.Data<API.DormitoryManagerComputedBaseMessage> & API.ResponseException>(
    dormitoryManagerBaseMessage,
    {
      data,
      ...(options || {}),
    },
  );
}

/** 获取社区居民楼片长图表数据 */
export async function queryDormitoryManagerChart(
  data?: API.DashboardComputedPostData,
  options?: Record<string, any>,
) {
  return request.post<API.Data<ColumnConfig> & API.ResponseException>(dormitoryManagerChart, {
    data,
    ...(options || {}),
  });
}

/** 获取社区分包人图表数据 */
export async function querySubcontractorChart(
  data?: API.DashboardComputedPostData,
  options?: Record<string, any>,
) {
  return request.post<API.Data<ColumnConfig> & API.ResponseException>(system.subcontractorChart, {
    data,
    ...(options || {}),
  });
}
