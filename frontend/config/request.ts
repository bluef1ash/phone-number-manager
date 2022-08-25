import { account } from '@/services/api';
import { ExceptionCode } from '@/services/enums';
import { COOKIE_TOKEN_KEY } from '@config/constant';
import { message, notification } from 'antd';
import Cookies from 'js-cookie';
import { history } from 'umi';
import { extend, RequestOptionsInit } from 'umi-request';

const request = extend({
  async errorHandler(error) {
    const { response, data } = error;
    if (response && response.status && data) {
      switch (data.code) {
        case ExceptionCode.NOT_LOGGED:
          message.error('登录已过期，请重新登录！');
          Cookies.remove(COOKIE_TOKEN_KEY);
          history.push(account.login.substring(REACT_APP_API_BASE_URL.length));
          break;
        case ExceptionCode.METHOD_ARGUMENT_NOT_VALID:
          for (let key in data.exception) {
            notification['error']({
              message: data.exception[key],
            });
          }
          break;
        case ExceptionCode.FORBIDDEN:
          notification.error({
            message: data.message,
          });
          break;
        default:
          break;
      }
    } else if (!response) {
      message.error('您的网络发生异常，无法连接服务器！');
    }
    return data;
  },
  credentials: 'include',
  timeout: 5 * 60 * 1000,
});
request.interceptors.request.use(
  (
    url,
    options,
  ): {
    url?: string;
    options?: RequestOptionsInit;
  } => {
    const token = Cookies.get(COOKIE_TOKEN_KEY);
    if (token) {
      options.headers = {
        ...options.headers,
        ...(options.requestType === 'form'
          ? {}
          : {
              Accept: 'application/json',
              'Content-Type': 'application/json',
            }),
        Authorization: `Bearer_${token}`,
      };
    }
    return { url, options };
  },
);
request.interceptors.response.use((response: Response, options: RequestOptionsInit) => {
  if (response.headers.get('Content-Type') === 'application/octet-stream') {
    options.responseType = 'blob';
  }
  return response;
});
export default request;
