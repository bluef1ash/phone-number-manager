import { Button, Result } from 'antd';
import React from 'react';
import { history } from 'umi';
import type { ResultProps } from 'antd/lib/result';

const NoFoundPage: React.FC<ResultProps> = () => (
  <Result
    status="404"
    title="404找不到页面"
    subTitle="抱歉，您访问的页面不存在。"
    extra={
      <Button type="primary" onClick={() => history.push('/')}>
        {' '}
        返回首页{' '}
      </Button>
    }
  />
);

export default NoFoundPage;
