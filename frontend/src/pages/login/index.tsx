import Footer from '@/components/Footer';
import { login } from '@/services/account/api';
import { queryMenuData } from '@/services/permission/api';
import { useModel } from '@@/plugin-model/useModel';
import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { LoginForm, ProFormText } from '@ant-design/pro-form';
import { COOKIE_TOKEN_KEY } from '@config/constant';
import { Alert, message as msg } from 'antd';
import Cookies from 'js-cookie';
import React, { useState } from 'react';
import { history } from 'umi';
import styles from './index.less';

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
  const submit = async (values: API.LoginParams) => {
    const { code, token, currentUser, message } = await login({ ...values });
    if (code === 200) {
      msg.success('登录成功！');
      Cookies.set(COOKIE_TOKEN_KEY, token, { expires: 7 });
      const { code: codeState, menuData, components } = await queryMenuData(true);
      if (codeState === 200 && history) {
        await setInitialState({
          ...initialState,
          currentUser,
          menuData,
          components,
        });
        const { query } = history.location;
        const { redirect } = query as {
          redirect: string;
        };
        history.push(redirect || '/');
      }
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
