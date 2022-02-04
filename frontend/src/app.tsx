import { account, baseUrl } from '@/services/api';
import Footer from '@/components/Footer';
import RightContent from '@/components/RightContent';
import { BookOutlined, LinkOutlined } from '@ant-design/icons';
import type { MenuDataItem, Settings as LayoutSettings } from '@ant-design/pro-layout';
import { PageLoading } from '@ant-design/pro-layout';
import {
  SESSION_COMPONENTS_KEY,
  SESSION_MENU_KEY,
  SESSION_SYSTEM_USER_KEY,
  SESSION_TOKEN_KEY,
} from '@config/constant';
import type { RunTimeLayoutConfig } from 'umi';
import { history, Link } from 'umi';
import { queryMenuData } from '@/services/permission/api';
import moment from 'moment';

const isDev = process.env.NODE_ENV === 'development';
const loginPath = account.login.substring(baseUrl.length);

/** 获取用户信息比较慢的时候会展示一个 loading */
export const initialStateConfig = {
  loading: <PageLoading />,
};

export async function getInitialState(): Promise<{
  settings?: Partial<LayoutSettings>;
  currentUser?: API.SystemUser;
  menuData?: MenuDataItem[];
  fetchMenuData?: (display: boolean) => Promise<API.ResponseMenu | undefined>;
  components?: string[];
}> {
  const fetchMenuData = async (display: boolean) => {
    const { code, menuData, components } = await queryMenuData(display);
    if (code === 0) {
      localStorage.setItem(SESSION_MENU_KEY, JSON.stringify(menuData));
      localStorage.setItem(SESSION_COMPONENTS_KEY, JSON.stringify(components));
      return { code, menuData, components };
    }
    return undefined;
  };
  if (history.location.pathname !== loginPath) {
    const sessionSystemUser = localStorage.getItem(SESSION_SYSTEM_USER_KEY);
    let currentUser: API.SystemUser | undefined = undefined;
    if (sessionSystemUser !== 'undefined' && sessionSystemUser) {
      currentUser = JSON.parse(sessionSystemUser);
    }
    const sessionMenu = localStorage.getItem(SESSION_MENU_KEY);
    let menuData: MenuDataItem[] | undefined = undefined;
    if (sessionMenu !== 'undefined' && sessionMenu) {
      menuData = JSON.parse(sessionMenu);
    } else {
      const menu = await fetchMenuData(true);
      if (typeof menu !== 'undefined') {
        menuData = menu.menuData;
      }
    }
    const sessionPaths = localStorage.getItem(SESSION_COMPONENTS_KEY);
    let components = [];
    if (sessionPaths !== 'undefined' && sessionPaths) {
      components = JSON.parse(sessionPaths);
    }
    return {
      currentUser,
      menuData,
      fetchMenuData,
      settings: {},
      components,
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
      // 如果没有登录，重定向到 login
      if (!session && location.pathname !== loginPath) {
        history.push(loginPath);
      }
    },
    menu: {
      params: initialState,
      request: (params): Promise<MenuDataItem[]> =>
        new Promise<MenuDataItem[]>((resolve) => resolve(params.menuData)),
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
    menuHeaderRender: undefined,
    // 自定义 403 页面
    // unAccessible: <div>unAccessible</div>,
    // 增加一个 loading 的状态
    // childrenRender: (children) => {
    //   if (initialState.loading) return <PageLoading />;
    //   return children;
    // },
    ...initialState?.settings,
  };
};
