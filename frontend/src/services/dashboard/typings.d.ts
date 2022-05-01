declare module '*.less';
declare namespace API {
  type DashboardComputedPostData = {
    computedType?: number;
    companyIds?: number[];
    barChartTypeParam?: boolean;
  };
  type DashboardComputed = {
    currentSystemUser?: SystemUser;
    dormitory?: {
      barChart: ComputedBarChart;
      baseMessage: ComputedBaseMessage;
    };
    resident?: {
      barChart: ComputedBarChart;
      baseMessage: ComputedBaseMessage;
    };
  };
  type ComputedBaseMessage = {
    resident: {
      inputCount?: number;
      haveToCount?: number;
      needToCount?: number;
      inputHaveToRatio?: number;
      loading?: boolean;
    };
  };
  type ComputedBarChart = {};
}
