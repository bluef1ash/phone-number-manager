import type { PageContainerProps } from '@ant-design/pro-layout';
import { PageContainer } from '@ant-design/pro-layout';
import { Link } from '@umijs/preset-dumi/lib/theme';
import type { PageHeaderProps } from 'antd';
import React from 'react';

export type BreadcrumbRole = {
  breadcrumbName: string;
  path: string;
};

export type MainPageContainerProps = {
  headerProps?: Partial<PageHeaderProps> & {
    children?: React.ReactNode;
  };
  routes?: BreadcrumbRole[];
} & PageContainerProps;

const MainPageContainer: React.FC<MainPageContainerProps> = ({
  headerProps,
  routes,
  ...restProps
}) => (
  <PageContainer
    header={{
      breadcrumb: {
        itemRender(route, params, breadcrumbRoutes, paths) {
          return breadcrumbRoutes.indexOf(route) === breadcrumbRoutes.length - 1 ||
            route.path === '' ? (
            <span>{route.breadcrumbName}</span>
          ) : (
            <Link to={paths.join('/')}>{route.breadcrumbName}</Link>
          );
        },
        routes,
      },
      ...headerProps,
    }}
    {...restProps}
  />
);

export default MainPageContainer;
