import React, { useEffect, useState } from 'react';
import MainPageContainer from '@/components/MainPageContainer';
import { StatisticCard } from '@ant-design/pro-card';
import { queryCompanySelectList } from '@/services/company/api';
import { useModel } from '@@/plugin-model/useModel';
import SelectCascder from '@/components/SelectCascder';
import { queryDashboardComputed } from '@/services/dashboard/api';
import { ComputedDataTypes } from '@/services/enums';

const Welcome: React.FC = () => {
  const { initialState } = useModel('@@initialState');
  const [companySelectListState, setCompanySelectListState] = useState<API.SelectList[]>([]);
  const [residentBaseMessageCompanyIdsState, setResidentBaseMessageCompanyIdsState] = useState<
    number[]
  >([]);
  const [baseMessageState, setBaseMessageState] = useState<API.ComputedBaseMessage>({
    resident: {
      inputCount: 0,
      haveToCount: 0,
      needToCount: 0,
      inputHaveToRatio: 0,
      loading: false,
    },
  });

  const residentBaseMessageHandle = (baseMessage: API.ComputedBaseMessage['resident']) => {
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
        setBaseMessageState({
          resident: {
            ...baseMessageState.resident,
            loading: true,
          },
        });
        setCompanySelectListState((await queryCompanySelectList(parentIds)).data);
        const { resident } = (await queryDashboardComputed()).data;
        setBaseMessageState({
          resident: {
            ...residentBaseMessageHandle(
              resident?.baseMessage as API.ComputedBaseMessage['resident'],
            ),
            loading: false,
          },
        });
      }
    };
    querySelectListData().then();
  }, [initialState]);
  return (
    <MainPageContainer
      ghost
      header={{
        title: '',
      }}
    >
      {' '}
      <StatisticCard.Group
        title="社区居民录入基本信息"
        extra={
          <SelectCascder
            isNotProForm={true}
            selectState={companySelectListState}
            setSelectState={setCompanySelectListState}
            cascaderFieldProps={{
              multiple: true,
              placeholder: '请选择单位',
              onChange(value) {
                setResidentBaseMessageCompanyIdsState(value?.map((v) => v[v.length - 1] as number));
              },
              async onDropdownVisibleChange(value) {
                if (value === false) {
                  setBaseMessageState({
                    resident: { ...baseMessageState.resident, loading: true },
                  });
                  const { resident } = (
                    await queryDashboardComputed({
                      computedType: ComputedDataTypes.RESIDENT_BASE_MESSAGE.valueOf(),
                      companyIds: residentBaseMessageCompanyIdsState,
                    })
                  ).data;
                  setBaseMessageState({
                    resident: {
                      ...residentBaseMessageHandle(
                        resident?.baseMessage as API.ComputedBaseMessage['resident'],
                      ),
                      loading: false,
                    },
                  });
                }
              },
            }}
            querySelectList={async (value) => (await queryCompanySelectList([value])).data}
          />
        }
      >
        {' '}
        <StatisticCard
          statistic={{
            title: '已录入人数',
            value: baseMessageState.resident.inputCount,
          }}
          loading={baseMessageState.resident.loading}
        />{' '}
        <StatisticCard.Divider />{' '}
        <StatisticCard
          statistic={{
            title: '核定人数',
            value: baseMessageState.resident.haveToCount,
            status: 'default',
          }}
          loading={baseMessageState.resident.loading}
        />{' '}
        <StatisticCard.Divider />{' '}
        <StatisticCard
          statistic={{
            title: '需要录入数',
            value: baseMessageState.resident.needToCount,
            status: 'success',
          }}
          loading={baseMessageState.resident.loading}
        />{' '}
        <StatisticCard.Divider />{' '}
        <StatisticCard
          statistic={{
            title: '录入与核定比例',
            value: baseMessageState.resident.inputHaveToRatio + '%',
            status: 'processing',
          }}
          loading={baseMessageState.resident.loading}
        />{' '}
      </StatisticCard.Group>{' '}
    </MainPageContainer>
  );
};

export default Welcome;
