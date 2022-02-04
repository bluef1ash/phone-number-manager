import { extend, ExtendOptionsInit, RequestOptionsInit } from 'umi-request';
import { message } from 'antd';
import { history } from 'umi';
import { account, baseUrl } from '@/services/api';
import { SESSION_TOKEN_KEY } from '@config/constant';

// 异常处理程序
const errorHandler = async (error: ExtendOptionsInit) => {
  const { response, data } = error;
  const loginPath = account.login.substring(baseUrl.length);
  if (response && response.status && response.status === 401) {
    history.push(loginPath);
  } else if (!response) {
    message.error('您的网络发生异常，无法连接服务器');
  }
  return data;
};
// 配置request请求时的默认参数
const request = extend({
  errorHandler,
  // 默认错误处理
  credentials: 'include',
});
request.interceptors.request.use(
  (
    url,
    options,
  ): {
    url?: string;
    options?: RequestOptionsInit;
  } => {
    let token = localStorage.getItem(SESSION_TOKEN_KEY);
    if (token) {
      options.headers = {
        ...options.headers,
        Accept: 'application/json',
        'Content-Type': 'application/json',
        Authorization: `Bearer_${token}`,
      };
    }
    return { url, options };
  },
);
export default request;
