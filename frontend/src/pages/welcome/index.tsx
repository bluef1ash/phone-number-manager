import React, { useEffect, useState } from 'react';
import MainPageContainer from '@/components/MainPageContainer';
import { StatisticCard } from '@ant-design/pro-card';
import { queryCompanySelectList } from '@/services/company/api';
import { useModel } from '@@/plugin-model/useModel';
import { queryDashboardComputed } from '@/services/dashboard/api';
import { ComputedDataTypes } from '@/services/enums';
import ComputedStatisticCard from '@/components/ComputedStatisticCard';
import styles from './index.less';
import { Column } from '@ant-design/charts';

const Welcome: React.FC = () => {
  const { initialState } = useModel('@@initialState');
  const [companySelectListState, setCompanySelectListState] = useState<API.SelectList[]>([]);
  const [residentBaseMessageCompanyIdsState, setResidentBaseMessageCompanyIdsState] = useState<
    number[]
  >([]);
  const [residentBarChartCompanyIdsState, setResidentBarChartCompanyIdsState] = useState<number[]>(
    [],
  );
  const [dashboardComputedState, setDashboardComputedState] = useState<API.DashboardComputed>({
    resident: {
      baseMessage: {
        inputCount: 0,
        haveToCount: 0,
        needToCount: 0,
        inputHaveToRatio: 0,
        loading: false,
      },
      barChart: {
        xField: 'companyName',
        yField: 'personNumber',
        data: {},
      },
    },
  });

  const residentBaseMessageHandle = (baseMessage: API.ResidentComputedBaseMessage) => {
    baseMessage.needToCount =
      (baseMessage.haveToCount as number) - (baseMessage.inputCount as number);
    baseMessage.inputHaveToRatio =
      (baseMessage.inputCount as number) / (baseMessage.haveToCount as number);
    return baseMessage;
  };

  useEffect(() => {
    const querySelectListData = async () => {
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
        setDashboardComputedState({
          resident: {
            baseMessage: {
              ...(typeof dashboardComputedState.resident === 'undefined'
                ? {}
                : dashboardComputedState.resident.baseMessage),
              loading: true,
            },
          },
        });
        setCompanySelectListState((await queryCompanySelectList(parentIds)).data);
        const { resident } = (await queryDashboardComputed()).data;
        setDashboardComputedState({
          resident: {
            baseMessage: {
              ...residentBaseMessageHandle(
                resident?.baseMessage as API.ResidentComputedBaseMessage,
              ),
              loading: false,
            },
            barChart: {
              ...resident?.barChart,
              loading: false,
            },
          },
        });
      }
    };
    querySelectListData().then();
  }, []);
  return (
    <MainPageContainer
      ghost
      header={{
        title: '',
      }}
    >
      {' '}
      <ComputedStatisticCard
        title="社区居民录入基本信息"
        companySelectListState={companySelectListState}
        setCompanySelectListState={setCompanySelectListState}
        companyIdsState={residentBaseMessageCompanyIdsState}
        setCompanyIdsState={setResidentBaseMessageCompanyIdsState}
        setDataState={setDashboardComputedState}
        loading={{
          resident: {
            ...dashboardComputedState,
            baseMessage: {
              ...dashboardComputedState.resident,
              ...(typeof dashboardComputedState.resident === 'undefined'
                ? {}
                : dashboardComputedState.resident.baseMessage),
              loading: true,
            },
          },
        }}
        computedType={ComputedDataTypes.RESIDENT_BASE_MESSAGE}
        dataResults={(data) => ({
          ...dashboardComputedState,
          resident: {
            ...dashboardComputedState.resident,
            baseMessage: {
              ...residentBaseMessageHandle(
                data.resident?.baseMessage as API.ResidentComputedBaseMessage,
              ),
              loading: false,
            },
          },
        })}
      >
        {' '}
        <StatisticCard
          statistic={{
            title: '已录入人数',
            value: dashboardComputedState?.resident?.baseMessage?.inputCount,
          }}
          loading={dashboardComputedState?.resident?.baseMessage?.loading}
        />{' '}
        <StatisticCard.Divider />{' '}
        <StatisticCard
          statistic={{
            title: '核定人数',
            value: dashboardComputedState?.resident?.baseMessage?.haveToCount,
            status: 'default',
          }}
          loading={dashboardComputedState?.resident?.baseMessage?.loading}
        />{' '}
        <StatisticCard.Divider />{' '}
        <StatisticCard
          statistic={{
            title: '需要录入数',
            value: dashboardComputedState?.resident?.baseMessage?.needToCount,
            status: 'success',
          }}
          loading={dashboardComputedState?.resident?.baseMessage?.loading}
        />{' '}
        <StatisticCard.Divider />{' '}
        <StatisticCard
          statistic={{
            title: '录入与核定比例',
            value: dashboardComputedState?.resident?.baseMessage?.inputHaveToRatio + '%',
            status: 'processing',
          }}
          loading={dashboardComputedState?.resident?.baseMessage?.loading}
        />{' '}
      </ComputedStatisticCard>{' '}
      <ComputedStatisticCard
        title="社区居民统计图表"
        className={styles['statistic-card']}
        companySelectListState={companySelectListState}
        setCompanySelectListState={setCompanySelectListState}
        companyIdsState={residentBarChartCompanyIdsState}
        setCompanyIdsState={setResidentBarChartCompanyIdsState}
        setDataState={setDashboardComputedState}
        loading={{
          ...dashboardComputedState,
          resident: {
            ...dashboardComputedState.resident,
            barChart: {
              ...(typeof dashboardComputedState.resident === 'undefined'
                ? {}
                : dashboardComputedState.resident.barChart),
              loading: true,
            },
          },
        }}
        computedType={ComputedDataTypes.RESIDENT_BAR_CHART}
        dataResults={(data) => ({
          ...dashboardComputedState,
          resident: {
            ...dashboardComputedState.resident,
            barChart: {
              ...(typeof data.resident === 'undefined' ? {} : data.resident.barChart),
              loading: false,
            },
          },
        })}
      >
        {' '}
        <Column
          {...dashboardComputedState?.resident?.barChart}
          label={{
            position: 'middle',
            style: {
              fill: '#FFFFFF',
              opacity: 0.6,
            },
          }}
          xAxis={{
            label: {
              autoHide: false,
              autoRotate: true,
            },
          }}
        />{' '}
      </ComputedStatisticCard>{' '}
    </MainPageContainer>
  );
};

export default Welcome;
