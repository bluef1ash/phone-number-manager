declare module '*.less';
declare namespace API {
  import type { BarConfig, ColumnConfig, PieConfig } from '@ant-design/charts';

  type DashboardComputedPostData = {
    computedType?: number;
    companyIds?: number[];
    barChartTypeParam?: boolean;
  };
  type DashboardComputed = {
    currentSystemUser?: SystemUser;
    dormitory?: {
      baseMessage?: DormitoryComputedBaseMessage;
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
  type DormitoryComputedBaseMessage = {
    inputCount?: number;
    genderCount?: BarConfig;
    ageCount?: PieConfig;
    educationCount?: PieConfig;
    politicalStatusCount?: PieConfig;
    employmentStatusCount?: PieConfig;
    loading?: boolean;
  };
}
