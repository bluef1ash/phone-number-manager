import Footer from '@/components/Footer';
import RightContent from '@/components/RightContent';
import { account } from '@/services/api';
import { BookOutlined,LinkOutlined } from '@ant-design/icons';
import type { MenuDataItem,Settings as LayoutSettings } from '@ant-design/pro-layout';
import { PageLoading } from '@ant-design/pro-layout';
import {
SESSION_COMPONENTS_KEY,
SESSION_MENU_KEY,
SESSION_SYSTEM_USER_KEY,
SESSION_TOKEN_KEY
} from '@config/constant';
import moment from 'moment';
import type { RunTimeLayoutConfig } from 'umi';
import { history,Link } from 'umi';
import { fetchMenuData } from './services/utils';

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
    const sessionSystemUser = localStorage.getItem(SESSION_SYSTEM_USER_KEY);
    let currentUser: API.SystemUser | undefined = undefined;
    if (sessionSystemUser !== null && sessionSystemUser !== 'undefined' && sessionSystemUser) {
      currentUser = JSON.parse(sessionSystemUser);
    }
    const sessionMenu = localStorage.getItem(SESSION_MENU_KEY);
    const sessionComponents = localStorage.getItem(SESSION_COMPONENTS_KEY);
    let menuData: MenuDataItem[] | undefined = [];
    let components: string[] | undefined = [];
    const isStorage =
      sessionMenu === null ||
      sessionMenu === 'undefined' ||
      sessionComponents === null ||
      sessionComponents === 'undefined';
    if (isStorage) {
      const result = await fetchMenuData(true);
      if (result?.code === 200) {
        menuData = result.menuData;
        components = result.components;
      }
    } else {
      menuData = JSON.parse(sessionMenu);
      components = JSON.parse(sessionComponents);
    }
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
      const session = localStorage.getItem(SESSION_TOKEN_KEY);
      if (!session && location.pathname !== loginPath) {
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
    // 自定义 403 页面
    // unAccessible: <div>unAccessible</div>,
    // 增加一个 loading 的状态
    childrenRender: (children) => {
      if (initialState?.loading) return <PageLoading />;
      return children;
    },
    ...initialState?.settings,
  };
};
