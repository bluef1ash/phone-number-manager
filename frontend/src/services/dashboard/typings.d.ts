declare module '*.less';
declare namespace API {
  import type { BarConfig, PieConfig } from '@ant-design/charts';

  type DashboardComputedPostData = {
    computedType?: number;
    companyIds?: number[];
    barChartTypeParam?: boolean;
  };
  type CommunityResidentComputedBaseMessage = {
    inputCount?: number;
    haveToCount?: number;
    needToCount?: number;
    inputHaveToRatio?: string;
    loading?: boolean;
  };
  type DormitoryManagerComputedBaseMessage = {
    inputCount?: number;
    genderCount?: BarConfig;
    ageCount?: PieConfig;
    educationCount?: PieConfig;
    politicalStatusCount?: PieConfig;
    employmentStatusCount?: PieConfig;
    loading?: boolean;
  };
}
