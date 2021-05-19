import axios, { AxiosError, AxiosResponse } from 'axios'
import { message } from 'antd' //响应时间

//响应时间
axios.defaults.timeout = 5000
//响应时间
// axios.defaults.headers.referer = 'http://drugsaffair.jd.com';
// 配置请求头
axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8'
let userData = localStorage.getItem('userData')
if (userData) {
  axios.defaults.headers.common['Authorization'] = 'Bearer ' //+ Vue.$cookies.get(USER_TOKEN_KEY)
}
//配置接口地址
axios.interceptors.response.use(
  (response: AxiosResponse) => response,
  (reason: AxiosError) => {
    message.error(reason.response.data.message).then(r => console.log(r))
    return Promise.reject(reason)
  }
)
export default axios
