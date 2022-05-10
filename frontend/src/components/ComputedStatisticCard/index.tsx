import React from 'react';
import SelectCascder from '@/components/SelectCascder';
import { queryDashboardComputed } from '@/services/dashboard/api';
import { queryCompanySelectList } from '@/services/company/api';
import type { StatisticCardProps } from '@ant-design/pro-card/lib/components/StatisticCard';
import StatisticCard from '@ant-design/pro-card/lib/components/StatisticCard';
import type { ComputedDataTypes } from '@/services/enums';

export type ComputedStatisticCardProps<T> = {
  companySelectListState: API.SelectList[];
  setCompanySelectListState: React.Dispatch<React.SetStateAction<API.SelectList[]>>;
  companyIdsState: number[];
  setCompanyIdsState: React.Dispatch<React.SetStateAction<number[]>>;
  setDataState: React.Dispatch<React.SetStateAction<T>>;
  loading: T;
  computedType: ComputedDataTypes;
  dataResults: (data: API.DashboardComputed) => T;
} & StatisticCardProps;

function ComputedStatisticCard<T>({
  companySelectListState,
  setCompanySelectListState,
  companyIdsState,
  setCompanyIdsState,
  setDataState,
  loading,
  computedType,
  dataResults,
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
            onChange(value) {
              setCompanyIdsState(value?.map((v) => v[v.length - 1] as number));
            },
            async onDropdownVisibleChange(value) {
              if (value === false) {
                setDataState(loading);
                const result = (
                  await queryDashboardComputed({
                    computedType: computedType.valueOf(),
                    companyIds: companyIdsState,
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
