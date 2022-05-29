import React from 'react';
import SelectCascder from '@/components/SelectCascder';
import { queryCompanySelectList } from '@/services/company/api';
import type { StatisticCardProps } from '@ant-design/pro-card/lib/components/StatisticCard';
import StatisticCard from '@ant-design/pro-card/lib/components/StatisticCard';

export type ComputedStatisticCardProps<T> = {
  companySelectListState: API.SelectList[];
  setCompanySelectListState: React.Dispatch<React.SetStateAction<API.SelectList[]>>;
  companySelectState?: API.SelectList[];
  setCompanySelectState: React.Dispatch<React.SetStateAction<API.SelectList[]>>;
  setDataState?: React.Dispatch<React.SetStateAction<T>>;
  loading?: T;
  dataResults?: (data: T) => T;
  queryDashboardComputed?: (
    data?: API.DashboardComputedPostData,
    options?: Record<string, any>,
  ) => Promise<API.Data<T>>;
  companySelectOnDropdownVisibleChange?: (open: boolean) => void;
} & StatisticCardProps;

function ComputedStatisticCard<T>({
  companySelectListState,
  setCompanySelectListState,
  companySelectState,
  setCompanySelectState,
  setDataState,
  loading,
  dataResults,
  queryDashboardComputed,
  companySelectOnDropdownVisibleChange,
  ...restProps
}: ComputedStatisticCardProps<T>) {
  return (
    <StatisticCard.Group
      extra={
        <SelectCascder
          isNotProForm={true}
          selectState={companySelectListState}
          setSelectState={setCompanySelectListState}
          cascaderFieldProps={{
            multiple: true,
            placeholder: '请选择单位',
            onChange(value, selectedOptions) {
              setCompanySelectState(
                selectedOptions?.map(
                  (selectedOption) => selectedOption[selectedOption.length - 1] as API.SelectList,
                ),
              );
            },
            onDropdownVisibleChange:
              typeof companySelectOnDropdownVisibleChange !== 'undefined'
                ? companySelectOnDropdownVisibleChange
                : async (open) => {
                    if (
                      open === false &&
                      typeof setDataState !== 'undefined' &&
                      typeof loading !== 'undefined' &&
                      typeof queryDashboardComputed !== 'undefined' &&
                      typeof dataResults !== 'undefined'
                    ) {
                      setDataState(loading);
                      const result = (
                        await queryDashboardComputed({
                          companyIds: companySelectState?.map((v) => v.id),
                        })
                      ).data;
                      setDataState(dataResults(result));
                    }
                  },
          }}
          querySelectList={async (value) => (await queryCompanySelectList([value])).data}
        />
      }
      {...restProps}
    />
  );
}

export default ComputedStatisticCard;
