import Footer from '@/components/Footer';
import RightContent from '@/components/RightContent';
import { account } from '@/services/api';
import { queryMenuData } from '@/services/permission/api';
import { queryCurrentUser } from '@/services/user/api';
import { BookOutlined, LinkOutlined } from '@ant-design/icons';
import type { MenuDataItem, Settings as LayoutSettings } from '@ant-design/pro-layout';
import { PageLoading } from '@ant-design/pro-layout';
import { COOKIE_TOKEN_KEY } from '@config/constant';
import Cookies from 'js-cookie';
import moment from 'moment';
import type { RunTimeLayoutConfig } from 'umi';
import { history, Link } from 'umi';

const isDev = process.env.NODE_ENV === 'development';
const loginPath = account.login.substring(REACT_APP_API_BASE_URL.length);

export const initialStateConfig = {
  loading: <PageLoading />,
};

export async function getInitialState(): Promise<{
  settings?: Partial<LayoutSettings>;
  currentUser?: API.SystemUser;
  menuData?: MenuDataItem[];
  components?: string[];
  loading?: boolean;
}> {
  if (history.location.pathname !== loginPath) {
    const { data: currentUser } = await queryCurrentUser();
    const { components, menuData } = await queryMenuData();
    return {
      currentUser,
      menuData,
      settings: {},
      components,
      loading: false,
    };
  }
  return {
    settings: {},
  };
}

export const layout: RunTimeLayoutConfig = ({ initialState }) => {
  moment.locale('zh-cn');
  return {
    rightContentRender: () => <RightContent />,
    disableContentMargin: false,
    waterMarkProps: {},
    footerRender: () => <Footer />,
    onPageChange: () => {
      const { location } = history;
      if (!Cookies.get(COOKIE_TOKEN_KEY) && location.pathname !== loginPath) {
        history.push(loginPath);
      }
    },
    menu: {
      params: initialState,
      request: (params) => new Promise<MenuDataItem[]>((resolve) => resolve(params.menuData)),
      locale: false,
    },
    links: isDev
      ? [
          <Link to="/umi/plugin/openapi" target="_blank" key="openapi">
            {' '}
            <LinkOutlined /> <span>OpenAPI 文档</span>{' '}
          </Link>,
          <Link to="/~docs" key="docs">
            {' '}
            <BookOutlined /> <span>业务组件文档</span>{' '}
          </Link>,
        ]
      : [],
    menuHeaderRender: false,
    childrenRender: (children) => {
      if (initialState?.loading) return <PageLoading />;
      return children;
    },
    ...initialState?.settings,
  };
};
