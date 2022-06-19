import Footer from '@/components/Footer';
import { login } from '@/services/account/api';
import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { LoginForm, ProFormText } from '@ant-design/pro-form';
import {
  SESSION_COMPONENTS_KEY,
  SESSION_MENU_KEY,
  SESSION_SYSTEM_USER_KEY,
  SESSION_TOKEN_KEY,
} from '@config/constant';
import { Alert, message as msg } from 'antd';
import React, { useState } from 'react';
import { history } from 'umi';
import styles from './index.less';
import { useModel } from '@@/plugin-model/useModel';
import { queryMenuData } from '@/services/permission/api';

const LoginMessage: React.FC<{
  content: string;
}> = ({ content }) => (
  <Alert
    style={{
      marginBottom: 24,
    }}
    message={content}
    type="error"
    showIcon
  />
);
const Login: React.FC = () => {
  const [userLoginState, setUserLoginState] = useState<API.ResponseException>({
    code: 200,
    message: '',
  });
  const { initialState, setInitialState } = useModel('@@initialState');
  const fetchMenuData = async (display: boolean, currentUser: API.SystemUser) => {
    const { code, menuData, components } = await queryMenuData(display);
    if (code === 200) {
      localStorage.setItem(SESSION_MENU_KEY, JSON.stringify(menuData));
      localStorage.setItem(SESSION_COMPONENTS_KEY, JSON.stringify(components));
      setInitialState({
        ...initialState,
        components,
        menuData,
        currentUser,
      });
    }
    return { code, menuData, components };
  };
  const submit = async (values: API.LoginParams) => {
    const { code, token, systemUserInfo, message } = await login({ ...values });
    if (code === 200) {
      msg.success('登录成功！');
      localStorage.setItem(SESSION_TOKEN_KEY, token);
      localStorage.setItem(SESSION_SYSTEM_USER_KEY, JSON.stringify(systemUserInfo));
      const { code: codeState } = await fetchMenuData(true, systemUserInfo as API.SystemUser);
      if (codeState !== 200 && !history) {
        return;
      }
      const { query } = history.location;
      const { redirect } = query as {
        redirect: string;
      };
      history.push(redirect || '/');
    } else {
      setUserLoginState({ code, message });
    }
  };
  const { code, message } = userLoginState;
  return (
    <div className={styles.container}>
      <div className={styles.content}>
        <LoginForm
          title="社区居民联系方式管理系统"
          subTitle=" "
          onFinish={async (values) => await submit(values as API.LoginParams)}
        >
          {' '}
          {code !== 200 && <LoginMessage content={message as string} />}{' '}
          <ProFormText
            name="username"
            fieldProps={{
              size: 'large',
              prefix: <UserOutlined className={styles.prefixIcon} />,
            }}
            placeholder={'用户名称'}
            rules={[
              {
                required: true,
                message: '用户名称是必填项！',
              },
            ]}
          />{' '}
          <ProFormText.Password
            name="password"
            fieldProps={{
              size: 'large',
              prefix: <LockOutlined className={styles.prefixIcon} />,
            }}
            placeholder={'用户密码'}
            rules={[
              {
                required: true,
                message: '用户密码是必填项！',
              },
            ]}
          />{' '}
          <a className={styles['forgot-password']}> 忘记密码？ </a>{' '}
        </LoginForm>
      </div>
      <Footer />
    </div>
  );
};
export default Login;
