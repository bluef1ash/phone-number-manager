import { account, baseUrl } from '@/services/api';
import { logout } from '@/services/account/api';
import { LogoutOutlined, SettingOutlined, UserOutlined } from '@ant-design/icons';
import {
  SESSION_COMPONENTS_KEY,
  SESSION_MENU_KEY,
  SESSION_SYSTEM_USER_KEY,
  SESSION_TOKEN_KEY,
} from '@config/constant';
import { Avatar, Menu, Spin } from 'antd';
import { stringify } from 'querystring';
import type { MenuInfo } from 'rc-menu/lib/interface';
import React, { useCallback } from 'react';
import { history, useModel } from 'umi';
import HeaderDropdown from '../HeaderDropdown';
import styles from './index.less';

export type GlobalHeaderRightProps = {
  menu?: boolean;
};

/**
 * 退出登录，并且将当前的 url 保存
 */
const out = async (loginUri: string) => {
  await logout();
  localStorage.removeItem(SESSION_TOKEN_KEY);
  localStorage.removeItem(SESSION_SYSTEM_USER_KEY);
  localStorage.removeItem(SESSION_MENU_KEY);
  localStorage.removeItem(SESSION_COMPONENTS_KEY);
  const { query = {}, search, pathname } = history.location;
  const { redirect } = query;
  if (window.location.pathname !== loginUri && !redirect) {
    history.replace({
      pathname: loginUri,
      search: stringify({
        redirect: pathname + search,
      }),
    });
  }
};

const AvatarDropdown: React.FC<GlobalHeaderRightProps> = ({ menu }) => {
  const { initialState, setInitialState } = useModel('@@initialState');
  const onMenuClick = useCallback(
    async (event: MenuInfo) => {
      const { key } = event;
      const loginUri = account.login.substring(baseUrl.length);
      if (key === 'logout') {
        await out(loginUri);
      }
      history.push(loginUri);
    },
    [setInitialState],
  );
  const loading = (
    <span className={`${styles.action} ${styles.account}`}>
      <Spin
        size="small"
        style={{
          marginLeft: 8,
          marginRight: 8,
        }}
      />
    </span>
  );
  if (!initialState) {
    return loading;
  }
  const { currentUser } = initialState;
  if (!currentUser || !currentUser.username) {
    return loading;
  }
  const menuHeaderDropdown = (
    <Menu className={styles.menu} selectedKeys={[]} onClick={onMenuClick}>
      {menu && (
        <Menu.Item key="center">
          {' '}
          <UserOutlined /> 个人中心{' '}
        </Menu.Item>
      )}{' '}
      {menu && (
        <Menu.Item key="settings">
          {' '}
          <SettingOutlined /> 个人设置{' '}
        </Menu.Item>
      )}{' '}
      {menu && <Menu.Divider />}{' '}
      <Menu.Item key="logout">
        {' '}
        <LogoutOutlined /> 退出登录{' '}
      </Menu.Item>{' '}
    </Menu>
  );
  return (
    <HeaderDropdown overlay={menuHeaderDropdown}>
      <span className={`${styles.action} ${styles.account}`}>
        <Avatar size="small" className={styles.avatar} alt="avatar" />
        <span className={`${styles.name} anticon`}>{currentUser.username}</span>
      </span>{' '}
    </HeaderDropdown>
  );
};

export default AvatarDropdown;
