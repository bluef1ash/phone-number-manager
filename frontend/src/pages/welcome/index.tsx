import React from 'react';
import MainPageContainer from '@/components/MainPageContainer';

const Welcome: React.FC = () => {
  return (
    <MainPageContainer
      routes={[
        {
          path: '/welcome',
          breadcrumbName: '首页',
        },
      ]}
    />
  );
};

export default Welcome;
