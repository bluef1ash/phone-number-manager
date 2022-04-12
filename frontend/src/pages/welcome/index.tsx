import React from 'react';
import MainPageContainer from '@/components/MainPageContainer';
import { StatisticCard } from '@ant-design/pro-card';
import { ProFormSelect } from '@ant-design/pro-form';

const Welcome: React.FC = () => {
  return (
    <MainPageContainer
      ghost
      header={{
        title: '',
      }}
    >
      {' '}
      <StatisticCard title="社区居民录入基本信息" extra={<ProFormSelect />} />{' '}
    </MainPageContainer>
  );
};

export default Welcome;
