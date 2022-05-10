declare module '*.less';
declare namespace API {
  import type { ColumnConfig } from '@ant-design/charts';

  type DashboardComputedPostData = {
    computedType?: number;
    companyIds?: number[];
    barChartTypeParam?: boolean;
  };
  type DashboardComputed = {
    currentSystemUser?: SystemUser;
    dormitory?: {
      baseMessage?: ResidentComputedBaseMessage;
      barChart?: ComputedBarChart;
    };
    resident?: {
      baseMessage?: ResidentComputedBaseMessage;
      barChart?: ColumnConfig;
    };
  };
  type ResidentComputedBaseMessage = {
    inputCount?: number;
    haveToCount?: number;
    needToCount?: number;
    inputHaveToRatio?: string;
    loading?: boolean;
  };
}
