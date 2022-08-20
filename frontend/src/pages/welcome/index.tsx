import ComputedChartColumn from '@/components/ComputedChartColumn';
import ComputedStatisticCard from '@/components/ComputedStatisticCard';
import MainPageContainer from '@/components/MainPageContainer';
import { queryCompanySelectList } from '@/services/company/api';
import {
  queryCommunityResidentBaseMessage,
  queryCommunityResidentChart,
  queryDormitoryManagerBaseMessage,
  queryDormitoryManagerChart,
  querySubcontractorChart,
} from '@/services/dashboard/api';
import { checkVisibleInDocument } from '@/services/utils';
import { useModel } from '@@/plugin-model/useModel';
import type { ColumnConfig } from '@ant-design/charts';
import { Bar, Pie } from '@ant-design/charts';
import { StatisticCard } from '@ant-design/pro-card';
import { Col, message, Row } from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import styles from './index.less';

const Welcome: React.FC = () => {
  const { initialState } = useModel('@@initialState');
  const [companySelectListState, setCompanySelectListState] = useState<API.SelectList[]>([]);
  const [notSubordinateCompanySelectListState, setNotSubordinateCompanySelectListState] = useState<
    API.SelectList[]
  >([]);
  const [residentBaseMessageCompaniesState, setResidentBaseMessageCompaniesState] = useState<
    API.SelectList[]
  >([]);
  const [residentBarChartCompaniesState, setResidentBarChartCompaniesState] = useState<
    API.SelectList[]
  >([]);
  const [dormitoryBaseMessageCompaniesState, setDormitoryBaseMessageCompaniesState] = useState<
    API.SelectList[]
  >([]);
  const [dormitoryBarChartCompaniesState, setDormitoryBarChartCompaniesState] = useState<
    API.SelectList[]
  >([]);
  const [subcontractorBarChartCompaniesState, setSubcontractorBarChartCompaniesState] = useState<
    API.SelectList[]
  >([]);
  const [residentBaseMessageState, setResidentBaseMessageState] =
    useState<API.CommunityResidentComputedBaseMessage>({
      inputCount: 0,
      haveToCount: 0,
      needToCount: 0,
      inputHaveToRatio: '0%',
      loading: true,
    });
  const [residentBarChartState, setResidentBarChartState] = useState<ColumnConfig>({
    xField: 'companyName',
    yField: 'personCount',
    data: [],
    loading: true,
  });
  const [dormitoryBaseMessageState, setDormitoryBaseMessageState] =
    useState<API.DormitoryManagerComputedBaseMessage>({
      inputCount: 0,
      genderCount: {
        xField: 'genderType',
        yField: 'value',
        data: [],
        loading: true,
      },
      ageCount: {
        data: [],
        angleField: 'value',
        colorField: 'ageType',
      },
      educationCount: {
        data: [],
        angleField: 'value',
        colorField: 'educationType',
      },
      politicalStatusCount: {
        data: [],
        angleField: 'value',
        colorField: 'politicalStatusType',
      },
      employmentStatusCount: {
        data: [],
        angleField: 'value',
        colorField: 'employmentStatusType',
      },
      loading: true,
    });
  const [dormitoryBarChartState, setDormitoryBarChartState] = useState<ColumnConfig>({
    xField: 'companyName',
    yField: 'personCount',
    data: [],
    loading: true,
  });
  const [subcontractorBarChartState, setSubcontractorBarChartState] = useState<ColumnConfig>({
    xField: 'companyName',
    yField: 'personCount',
    data: [],
    loading: true,
  });
  const [loadingsState, setLoadingsState] = useState<boolean[]>([true, true, true, true, true]);
  const communityResidentComputedBaseMessageRef = useRef<HTMLDivElement>(null);
  const communityResidentComputedChartRef = useRef<HTMLDivElement>(null);
  const dormitoryManagerComputedBaseMessageRef = useRef<HTMLDivElement>(null);
  const dormitoryManagerComputedChartRef = useRef<HTMLDivElement>(null);
  const subcontractorComputedChartRef = useRef<HTMLDivElement>(null);
  const [isHideSelectState, setIsHideSelectState] = useState<boolean>(false);

  const residentBaseMessageHandle = (baseMessage: API.CommunityResidentComputedBaseMessage) => {
    if (
      typeof baseMessage.inputCount !== 'undefined' &&
      typeof baseMessage.haveToCount !== 'undefined'
    ) {
      baseMessage.needToCount = baseMessage.haveToCount - baseMessage.inputCount;
      baseMessage.inputHaveToRatio =
        baseMessage.haveToCount !== 0
          ? `${Math.round((baseMessage.inputCount / baseMessage.haveToCount) * 10000) / 100}%`
          : '无法计算';
    }
    return baseMessage;
  };

  const loadData = async () => {
    if (checkVisibleInDocument(communityResidentComputedBaseMessageRef) && loadingsState[0]) {
      loadingsState[0] = false;
      setLoadingsState([...loadingsState]);
      setResidentBaseMessageState(
        residentBaseMessageHandle((await queryCommunityResidentBaseMessage()).data),
      );
    }
    if (checkVisibleInDocument(communityResidentComputedChartRef) && loadingsState[1]) {
      loadingsState[1] = false;
      setLoadingsState([...loadingsState]);
      setResidentBarChartState((await queryCommunityResidentChart()).data);
    }
    if (checkVisibleInDocument(dormitoryManagerComputedBaseMessageRef) && loadingsState[2]) {
      loadingsState[2] = false;
      setLoadingsState([...loadingsState]);
      setDormitoryBaseMessageState((await queryDormitoryManagerBaseMessage()).data);
    }
    if (checkVisibleInDocument(dormitoryManagerComputedChartRef) && loadingsState[3]) {
      loadingsState[3] = false;
      setLoadingsState([...loadingsState]);
      setDormitoryBarChartState((await queryDormitoryManagerChart()).data);
    }
    if (checkVisibleInDocument(subcontractorComputedChartRef) && loadingsState[4]) {
      loadingsState[4] = false;
      setLoadingsState([...loadingsState]);
      setSubcontractorBarChartState((await querySubcontractorChart()).data);
    }
  };

  useEffect(() => {
    window.addEventListener('scroll', async () => await loadData());
    (async () => {
      if (initialState) {
        const currentUser = initialState.currentUser;
        setIsHideSelectState(
          currentUser?.companies?.some((company: API.Company) => company.isLeaf),
        );
        let parentIds = [0];
        if (
          typeof currentUser.companies !== 'undefined' &&
          currentUser.companies !== null &&
          currentUser.companies.length > 0
        ) {
          parentIds = currentUser.companies.map(
            (company: API.Company) => company.parentId as number,
          );
        }
        const companySelects = (await queryCompanySelectList(parentIds)).data;
        setCompanySelectListState(companySelects);
        setNotSubordinateCompanySelectListState(
          companySelects.map((companySelect: API.SelectList) => {
            const companySelected = { ...companySelect };
            companySelected.isLeaf = !companySelected.isGrandsonLevel;
            return companySelected;
          }),
        );
        await loadData();
      }
    })();
    return () => {
      window.removeEventListener('scroll', async () => await loadData());
    };
  }, []);

  return (
    <MainPageContainer
      ghost
      header={{
        title: '',
      }}
    >
      {' '}
      <div ref={communityResidentComputedBaseMessageRef}>
        <ComputedStatisticCard<API.CommunityResidentComputedBaseMessage>
          title="社区居民录入基本信息"
          companySelectListState={companySelectListState}
          setCompanySelectListState={setCompanySelectListState}
          companySelectState={residentBaseMessageCompaniesState}
          setCompanySelectState={setResidentBaseMessageCompaniesState}
          setDataState={setResidentBaseMessageState}
          loadingObject={{
            ...residentBaseMessageState,
            loading: true,
          }}
          queryDashboardComputed={queryCommunityResidentBaseMessage}
          dataResults={(data) => ({
            ...residentBaseMessageHandle(data),
          })}
          isHideSelect={isHideSelectState}
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
      </div>
      <div ref={communityResidentComputedChartRef}>
        <ComputedStatisticCard<ColumnConfig>
          title="社区居民人数统计图表"
          className={styles['statistic-card']}
          companySelectListState={notSubordinateCompanySelectListState}
          setCompanySelectListState={setNotSubordinateCompanySelectListState}
          companySelectState={residentBarChartCompaniesState}
          setCompanySelectState={setResidentBarChartCompaniesState}
          setDataState={setResidentBarChartState}
          loadingObject={{
            ...residentBarChartState,
            loading: true,
          }}
          dataResults={(data) => ({
            ...data,
            loading: false,
          })}
          queryDashboardComputed={queryCommunityResidentChart}
          isHideSelect={isHideSelectState}
        >
          {' '}
          <ComputedChartColumn {...residentBarChartState} />{' '}
        </ComputedStatisticCard>{' '}
      </div>
      <div ref={dormitoryManagerComputedBaseMessageRef}>
        <ComputedStatisticCard<API.DormitoryManagerComputedBaseMessage>
          title="社区居民楼片长录入基本信息"
          className={styles['statistic-card']}
          companySelectListState={companySelectListState}
          setCompanySelectListState={setCompanySelectListState}
          companySelectState={dormitoryBaseMessageCompaniesState}
          setCompanySelectState={setDormitoryBaseMessageCompaniesState}
          setDataState={setDormitoryBaseMessageState}
          loadingObject={{
            ...dormitoryBaseMessageState,
            loading: true,
          }}
          dataResults={(data) => ({
            ...data,
          })}
          queryDashboardComputed={queryDormitoryManagerBaseMessage}
          isHideSelect={isHideSelectState}
        >
          {' '}
          <Row justify="space-around" className={styles['dormitory-message']}>
            {' '}
            <Col span={11}>
              {' '}
              <StatisticCard
                statistic={{
                  title: '已录入人数',
                  value: dormitoryBaseMessageState.inputCount,
                }}
                loading={dormitoryBaseMessageState.loading}
              />{' '}
            </Col>{' '}
            <Col span={11}>
              {' '}
              <Bar
                {...dormitoryBaseMessageState.genderCount}
                seriesField={dormitoryBaseMessageState.genderCount.yField}
                legend={{
                  position: 'top-left',
                }}
              />{' '}
            </Col>{' '}
          </Row>{' '}
          <Row justify="space-around" className={styles['dormitory-message']}>
            {' '}
            <Col span={11}>
              {' '}
              <Pie {...dormitoryBaseMessageState.ageCount} />{' '}
            </Col>{' '}
            <Col span={11}>
              {' '}
              <Pie {...dormitoryBaseMessageState.educationCount} />{' '}
            </Col>{' '}
          </Row>{' '}
          <Row justify="space-around" className={styles['dormitory-message']}>
            {' '}
            <Col span={11}>
              {' '}
              <Pie {...dormitoryBaseMessageState.politicalStatusCount} />{' '}
            </Col>{' '}
            <Col span={11}>
              {' '}
              <Pie {...dormitoryBaseMessageState.employmentStatusCount} />{' '}
            </Col>{' '}
          </Row>{' '}
        </ComputedStatisticCard>{' '}
      </div>
      <div ref={dormitoryManagerComputedChartRef}>
        <ComputedStatisticCard<ColumnConfig>
          title="社区居民楼片长人数统计图表"
          className={styles['statistic-card']}
          companySelectListState={notSubordinateCompanySelectListState}
          setCompanySelectListState={setNotSubordinateCompanySelectListState}
          companySelectState={dormitoryBarChartCompaniesState}
          setCompanySelectState={setDormitoryBarChartCompaniesState}
          setDataState={setDormitoryBarChartState}
          loadingObject={{
            ...dormitoryBarChartState,
            loading: true,
          }}
          dataResults={(data) => ({
            ...data,
          })}
          queryDashboardComputed={queryDormitoryManagerChart}
          isHideSelect={isHideSelectState}
        >
          {' '}
          <ComputedChartColumn {...dormitoryBarChartState} />{' '}
        </ComputedStatisticCard>{' '}
      </div>
      <div ref={subcontractorComputedChartRef}>
        <ComputedStatisticCard<ColumnConfig>
          title="社区分包人数统计图表"
          className={styles['statistic-card']}
          companySelectListState={companySelectListState}
          setCompanySelectListState={setCompanySelectListState}
          setCompanySelectState={setSubcontractorBarChartCompaniesState}
          companySelectOnDropdownVisibleChange={async (open) => {
            if (!open) {
              let tempSelect: API.SelectList | null = null;
              for (const subcontractorBarChartCompany of subcontractorBarChartCompaniesState) {
                if (
                  tempSelect !== null &&
                  tempSelect.isSubordinate !== subcontractorBarChartCompany.isSubordinate
                ) {
                  message.error('不允许同时选择基层单位与非基层单位！');
                  return;
                }
                tempSelect = subcontractorBarChartCompany;
              }
              setSubcontractorBarChartState({
                ...subcontractorBarChartState,
                loading: false,
              });
              const result = (
                await querySubcontractorChart({
                  companyIds: subcontractorBarChartCompaniesState.map((v) => v.id),
                })
              ).data;
              setSubcontractorBarChartState({ ...result });
            }
          }}
          isHideSelect={isHideSelectState}
        >
          {' '}
          <ComputedChartColumn {...subcontractorBarChartState} />{' '}
        </ComputedStatisticCard>{' '}
      </div>
    </MainPageContainer>
  );
};

export default Welcome;
