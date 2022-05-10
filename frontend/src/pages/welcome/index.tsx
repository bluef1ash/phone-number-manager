import React, { useEffect, useState } from 'react';
import MainPageContainer from '@/components/MainPageContainer';
import { StatisticCard } from '@ant-design/pro-card';
import { queryCompanySelectList } from '@/services/company/api';
import { useModel } from '@@/plugin-model/useModel';
import { queryDashboardComputed } from '@/services/dashboard/api';
import { ComputedDataTypes } from '@/services/enums';
import ComputedStatisticCard from '@/components/ComputedStatisticCard';
import styles from './index.less';
import type { ColumnConfig } from '@ant-design/charts';
import ComputedChartColumn from '@/components/ComputedChartColumn';

const Welcome: React.FC = () => {
  const { initialState } = useModel('@@initialState');
  const [companySelectListState, setCompanySelectListState] = useState<API.SelectList[]>([]);
  const [notSubordinateCompanySelectListState, setNotSubordinateCompanySelectListState] = useState<
    API.SelectList[]
  >([]);
  const [residentBaseMessageCompanyIdsState, setResidentBaseMessageCompanyIdsState] = useState<
    number[]
  >([]);
  const [residentBarChartCompanyIdsState, setResidentBarChartCompanyIdsState] = useState<number[]>(
    [],
  );
  const [residentBaseMessageState, setResidentBaseMessageState] =
    useState<API.ResidentComputedBaseMessage>({
      inputCount: 0,
      haveToCount: 0,
      needToCount: 0,
      inputHaveToRatio: '0%',
      loading: false,
    });
  const [residentBarChartState, setResidentBarChartState] = useState<ColumnConfig>({
    xField: 'companyName',
    yField: 'personCount',
    data: [],
    loading: false,
  });

  const residentBaseMessageHandle = (baseMessage: API.ResidentComputedBaseMessage) => {
    if (
      typeof baseMessage.inputCount !== 'undefined' &&
      typeof baseMessage.haveToCount !== 'undefined'
    ) {
      baseMessage.needToCount = baseMessage.haveToCount - baseMessage.inputCount;
      baseMessage.inputHaveToRatio =
        baseMessage.haveToCount !== 0
          ? `${baseMessage.inputCount / baseMessage.haveToCount}%`
          : '无法计算';
    }
    return baseMessage;
  };

  useEffect(() => {
    const queryData = async () => {
      if (initialState) {
        const currentUser = initialState.currentUser as API.SystemUser;
        let parentIds = [0];
        if (
          typeof currentUser.companies !== 'undefined' &&
          currentUser.companies !== null &&
          currentUser.companies.length > 0
        ) {
          parentIds = currentUser.companies.map((company) => company.parentId as number);
        }
        const companySelects = (await queryCompanySelectList(parentIds)).data;
        setCompanySelectListState(companySelects);
        setNotSubordinateCompanySelectListState(
          companySelects.map((companySelect) => {
            const companySelected = { ...companySelect };
            companySelected.isLeaf = !companySelected.isSubordinate;
            return companySelected;
          }),
        );
        setResidentBaseMessageState({
          ...(typeof residentBaseMessageState === 'undefined' ? {} : residentBaseMessageState),
          loading: true,
        });
        setResidentBarChartState({
          ...(typeof residentBarChartState === 'undefined'
            ? {
                xField: 'companyName',
                yField: 'personCount',
                data: [],
              }
            : residentBarChartState),
          loading: true,
        });
        const { resident } = (await queryDashboardComputed()).data;
        setResidentBaseMessageState({
          ...residentBaseMessageHandle(resident?.baseMessage as API.ResidentComputedBaseMessage),
          loading: false,
        });
        setResidentBarChartState({ ...resident?.barChart, loading: false });
      }
    };
    queryData().then();
  }, []);
  return (
    <MainPageContainer
      ghost
      header={{
        title: '',
      }}
    >
      {' '}
      <ComputedStatisticCard<API.ResidentComputedBaseMessage>
        title="社区居民录入基本信息"
        companySelectListState={companySelectListState}
        setCompanySelectListState={setCompanySelectListState}
        companyIdsState={residentBaseMessageCompanyIdsState}
        setCompanyIdsState={setResidentBaseMessageCompanyIdsState}
        setDataState={setResidentBaseMessageState}
        loading={{
          ...residentBaseMessageState,
          loading: true,
        }}
        computedType={ComputedDataTypes.RESIDENT_BASE_MESSAGE}
        dataResults={({ resident }) => ({
          ...residentBaseMessageHandle(resident?.baseMessage as API.ResidentComputedBaseMessage),
          loading: false,
        })}
      >
        {' '}
        <StatisticCard
          statistic={{
            title: '已录入人数',
            value: residentBaseMessageState.inputCount,
          }}
          loading={residentBaseMessageState.loading}
        />{' '}
        <StatisticCard.Divider />{' '}
        <StatisticCard
          statistic={{
            title: '核定人数',
            value: residentBaseMessageState.haveToCount,
            status: 'default',
          }}
          loading={residentBaseMessageState.loading}
        />{' '}
        <StatisticCard.Divider />{' '}
        <StatisticCard
          statistic={{
            title: '需要录入数',
            value: residentBaseMessageState.needToCount,
            status: 'success',
          }}
          loading={residentBaseMessageState.loading}
        />{' '}
        <StatisticCard.Divider />{' '}
        <StatisticCard
          statistic={{
            title: '录入与核定比例',
            value: residentBaseMessageState.inputHaveToRatio,
            status: 'processing',
          }}
          loading={residentBaseMessageState.loading}
        />{' '}
      </ComputedStatisticCard>{' '}
      <ComputedStatisticCard<ColumnConfig>
        title="社区居民人数统计图表"
        className={styles['statistic-card']}
        companySelectListState={notSubordinateCompanySelectListState}
        setCompanySelectListState={setCompanySelectListState}
        companyIdsState={residentBarChartCompanyIdsState}
        setCompanyIdsState={setResidentBarChartCompanyIdsState}
        setDataState={setResidentBarChartState}
        loading={{
          ...residentBarChartState,
          loading: true,
        }}
        computedType={ComputedDataTypes.RESIDENT_BAR_CHART}
        dataResults={({ resident }) => ({
          ...resident?.barChart,
          loading: false,
        })}
      >
        {' '}
        <ComputedChartColumn {...residentBarChartState} />{' '}
      </ComputedStatisticCard>{' '}
    </MainPageContainer>
  );
};

export default Welcome;
