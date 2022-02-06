import Footer from '@/components/Footer';
import { login } from '@/services/account/api';
import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { LoginForm, ProFormCheckbox, ProFormText } from '@ant-design/pro-form';
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
  const [userLoginState, setUserLoginState] = useState<API.ResponseException>({ code: 0 });
  const { setInitialState } = useModel('@@initialState');
  const fetchMenuData = async (display: boolean, currentUser: API.SystemUser) => {
    const { code, menuData, components } = await queryMenuData(display);
    if (code === 200) {
      localStorage.setItem(SESSION_MENU_KEY, JSON.stringify(menuData));
      localStorage.setItem(SESSION_COMPONENTS_KEY, JSON.stringify(components));
      await setInitialState((params) => ({
        components,
        menuData,
        currentUser,
        ...params,
      }));
    }
    return { code, menuData, components };
  };
  const handleSubmit = async (values: API.LoginParams) => {
    try {
      const data = await login({ ...values });
      if (data?.code === 200) {
        msg.success('登录成功！');
        localStorage.setItem(SESSION_TOKEN_KEY, data.token as string);
        localStorage.setItem(SESSION_SYSTEM_USER_KEY, JSON.stringify(data.systemUserInfo));
        await fetchMenuData(true, data.systemUserInfo as API.SystemUser);
        if (!history) {
          return;
        }
        const { query } = history.location;
        const { redirect } = query as {
          redirect: string;
        };
        history.push(redirect || '/');
        return;
      }
      setUserLoginState(data);
    } catch (error) {
      msg.error('登录失败，' + error + '！');
    }
  };
  const { code, message } = userLoginState;
  return (
    <div className={styles.container}>
      <div className={styles.content}>
        <LoginForm
          title="社区居民联系方式管理系统"
          initialValues={{
            autoLogin: true,
          }}
          onFinish={async (values) => {
            await handleSubmit(values as API.LoginParams);
          }}
        >
          <div style={{ marginTop: 24 }}>
            {' '}
            {typeof code !== 'undefined' && code !== 0 && (
              <LoginMessage content={message as string} />
            )}{' '}
            <ProFormText
              name="phoneNumber"
              fieldProps={{
                size: 'large',
                prefix: <UserOutlined className={styles.prefixIcon} />,
              }}
              placeholder={'手机号码'}
              rules={[
                {
                  required: true,
                  message: '手机号码是必填项！',
                },
              ]}
            />{' '}
            <ProFormText.Password
              name="password"
              fieldProps={{
                size: 'large',
                prefix: <LockOutlined className={styles.prefixIcon} />,
              }}
              placeholder={'密码'}
              rules={[
                {
                  required: true,
                  message: '密码是必填项！',
                },
              ]}
            />
            <div
              style={{
                marginBottom: 24,
              }}
            >
              <ProFormCheckbox noStyle name="autoLogin">
                {' '}
                自动登录{' '}
              </ProFormCheckbox>{' '}
              <a
                style={{
                  float: 'right',
                }}
              >
                {' '}
                忘记密码 ?{' '}
              </a>
            </div>
          </div>
        </LoginForm>
      </div>
      <Footer />
    </div>
  );
};
export default Login;
