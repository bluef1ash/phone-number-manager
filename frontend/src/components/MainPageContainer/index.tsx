import React from 'react';
import { Link } from '@umijs/preset-dumi/lib/theme';
import type { PageContainerProps } from '@ant-design/pro-layout';
import { PageContainer } from '@ant-design/pro-layout';

export type BreadcrumbRole = {
  breadcrumbName: string;
  path: string;
};

export type MainPageContainerProps = {
  routes?: BreadcrumbRole[];
} & PageContainerProps;

const MainPageContainer: React.FC<MainPageContainerProps> = ({ routes, ...restProps }) => (
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
    }}
    {...restProps}
  />
);

export default MainPageContainer;
