import { extend, ExtendOptionsInit, RequestOptionsInit } from 'umi-request';
import { message, notification } from 'antd';
import { history } from 'umi';
import { account, baseUrl } from '@/services/api';
import { SESSION_TOKEN_KEY } from '@config/constant';

const request = extend({
  errorHandler: async (error: ExtendOptionsInit) => {
    const { response, data } = error;
    if (response && response.status && data) {
      if (response.status === 401) {
        history.push(account.login.substring(baseUrl.length));
      } else if (data.code === 10005) {
        for (let key in data.exception) {
          notification['error']({
            message: data.exception[key],
          });
        }
      }
    } else if (!response) {
      message.error('您的网络发生异常，无法连接服务器');
    }
    return data;
  },
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
    const token = localStorage.getItem(SESSION_TOKEN_KEY);
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
request.interceptors.response.use(async (response: Response, options: RequestOptionsInit) => {
  if (response.headers.get('Content-Type') === 'application/octet-stream') {
    options.responseType = 'blob';
  }
  return response;
});
export default request;
