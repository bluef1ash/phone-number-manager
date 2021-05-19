import styles from '@/components/login/style.module.scss'
import { Button, Form, Input } from 'antd'
import { LockOutlined, UserOutlined } from '@ant-design/icons'
import { useEffect } from 'react'
import { account } from '@/config/route'
import axios from '@/config/axios'

export default function Login(): JSX.Element {
  const onFinish = (values: any) => {
    console.log('Received values of form: ', values)
  }
  useEffect(() => {
    axios(
      {
        url: account.getRecaptcha + '?browserType=' + this.browserType + '&time=' + new Date().getTime(),
        dataType: 'json'
      },
      result => {
        if (result.success === 1) {
          initGeetest(
            {
              gt: result.gt,
              challenge: result.challenge,
              product: 'bind',
              offline: !result.success,
              new_captcha: true
            },
            captchaObj => {
              this.captchaObj = captchaObj
              this.captchaObj.bindForm('#login_form')
              this.captchaObj
                .onReady(() => {
                  this.isCaptcha = 'none'
                })
                .onSuccess(() => {
                  $ajax(
                    {
                      url: this.$refs.loginForm.action,
                      method: 'post',
                      data: {
                        username: this.username,
                        password: sha256(this.password),
                        browserType: this.browserType
                      },
                      beforeSend: xmlHttpRequest => {
                        xmlHttpRequest.setRequestHeader('X-CSRF-TOKEN', this.csrf.prop('content'))
                        this.loading = this.$loading({
                          lock: true,
                          text: '正在登录，请稍等。。。',
                          spinner: 'el-icon-loading',
                          background: 'rgba(0, 0, 0, 0.8)'
                        })
                      }
                    },
                    data => {
                      let timeout = 5000
                      this.loading.close()
                      this.$message.success(data.message + timeout / 1000 + '秒后进入系统。。。')
                      setTimeout((location.href = '/'), timeout)
                    },
                    xhr => {
                      this.loading.close()
                      let responseJSON = xhr.responseJSON
                      this.$message.error(responseJSON.message)
                      let invalidIndex = 0
                      if (responseJSON.fieldName === 'password') {
                        invalidIndex = 1
                      } else if (responseJSON.fieldName === 'captcha') {
                        invalidIndex = 2
                        this.isCaptcha = 'block'
                      }
                      this.$set(this.isInvalids, invalidIndex, true)
                      this.$set(this.messages, invalidIndex, responseJSON.message)
                    }
                  )
                })
                .onError(() => {
                  this.$set(this.messages, 2, '加载图形验证码失败，请稍后再试！')
                })
            }
          )
        }
      }
    )
  }, [])

  return (
    <>
      <div className={styles.login}>
        <Form name="normal_login" className={styles['login-form']} initialValues={{ remember: true }} onFinish={onFinish}>
          <h3>社区居民联系方式管理系统</h3>
          <Form.Item name="username" rules={[{ required: true, message: '请输入用户名！' }]}>
            {' '} <Input prefix={<UserOutlined className="site-form-item-icon" />} placeholder="请输入用户名" />{' '}
          </Form.Item>{' '} <Form.Item name="password" rules={[
          {
            required: true,
            message: '请输入密码！'
          }
        ]}>
          {' '} <Input prefix={
          <LockOutlined className="site-form-item-icon" />} type="password" placeholder="请输入密码" />{' '}
        </Form.Item>{' '} <Form.Item>
          {' '} <a className="login-form-forgot" href="">
          {' '} 忘记密码{' '}
        </a>{' '}
        </Form.Item>{' '} <Form.Item>
          {' '} <Button type="primary" htmlType="submit" className="login-form-button">
          {' '} 登录{' '}
        </Button>{' '}
        </Form.Item>{' '}
        </Form>
        <div id="el-login-footer">© 2021 廿二月的天</div>
      </div>
    </>
  )
}
