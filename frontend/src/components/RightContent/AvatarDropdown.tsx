import UserForm, { UserFormSubmitPreHandler } from '@/components/UserForm';
import { logout } from '@/services/account/api';
import { account } from '@/services/api';
import { queryCompanySelectList } from '@/services/company/api';
import { modifySystemUser, queryCurrentUser } from '@/services/user/api';
import { getCompanyParentIds } from '@/services/utils';
import { LogoutOutlined, UserOutlined } from '@ant-design/icons';
import type { ProFormInstance } from '@ant-design/pro-form';
import { ModalForm } from '@ant-design/pro-form';
import { COOKIE_TOKEN_KEY } from '@config/constant';
import { Avatar, Menu, message as msg, Spin } from 'antd';
import Cookies from 'js-cookie';
import { stringify } from 'querystring';
import React, { useRef, useState } from 'react';
import { history, useModel } from 'umi';
import HeaderDropdown from '../HeaderDropdown';
import styles from './index.less';

//eslint-disable-next-line @typescript-eslint/ban-types
export type GlobalHeaderRightProps = {};

const AvatarDropdown: React.FC<GlobalHeaderRightProps> = () => {
  const { initialState, setInitialState } = useModel('@@initialState');
  const formRef = useRef<ProFormInstance<API.SystemUser>>();
  const [companySelectState, setCompanySelectState] = useState<API.SelectList[]>([]);
  const [systemUserState, setSystemUserState] = useState<API.SystemUser>();
  const [modalVisibleState, setModalVisibleState] = useState<boolean>(false);

  if (!initialState || !initialState.currentUser || !initialState.currentUser.username) {
    return (
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
  }
  const menuHeaderDropdown = (
    <Menu
      className={styles.menu}
      selectedKeys={[]}
      onClick={async (event) => {
        const { key } = event;
        const loginUri = account.login.substring(REACT_APP_API_BASE_URL.length);
        switch (key) {
          case 'logout':
            await logout();
            Cookies.remove(COOKIE_TOKEN_KEY);
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
            history.push(loginUri);
            break;
          case 'restPassword':
            setModalVisibleState(true);
            break;
          default:
            break;
        }
      }}
      items={[
        {
          key: 'restPassword',
          icon: <UserOutlined />,
          label: '修改密码',
        },
        {
          type: 'divider',
        },
        {
          key: 'logout',
          icon: <LogoutOutlined />,
          label: '退出登录',
        },
      ]}
    />
  );
  return (
    <>
      <HeaderDropdown overlay={menuHeaderDropdown}>
        <span className={`${styles.action} ${styles.account}`}>
          <Avatar size="small" className={styles.avatar} icon={<UserOutlined />} />
          <span className={`${styles.name} anticon`}>{initialState.currentUser.username}</span>
        </span>
      </HeaderDropdown>
      <ModalForm<API.SystemUser>
        formRef={formRef}
        width={600}
        visible={modalVisibleState}
        submitter={{
          searchConfig: {
            submitText: '保存',
            resetText: '重置',
          },
          resetButtonProps: {
            onClick: () => formRef?.current?.resetFields(),
          },
        }}
        autoFocusFirstInput={true}
        modalProps={{
          title: '系统用户修改密码',
          destroyOnClose: true,
          onCancel: () => setModalVisibleState(false),
        }}
        onFinish={async (formData) => {
          formData.id = systemUserState.id;
          const { code, message } = await modifySystemUser(
            formData.id,
            UserFormSubmitPreHandler(formData),
          );
          if (code === 200) {
            const {
              data: currentUser,
            } = await queryCurrentUser();
            await setInitialState({ ...initialState, currentUser });
            msg.success(message === 'success' ? '修改成功！' : message);
            setModalVisibleState(false);
            return true;
          }
          msg.error(message);
          return false;
        }}
        onVisibleChange={async (visible) => {
          if (visible) {
            const parentIds = getCompanyParentIds(await initialState.currentUser);
            setCompanySelectState((await queryCompanySelectList(parentIds)).data);
            const currentUser = initialState.currentUser;
            currentUser.phoneNumber = {
              phoneNumber: currentUser.phoneNumber,
            };
            if (
              typeof currentUser.companies !== 'undefined' &&
              currentUser.companies !== null &&
              currentUser.companies.length > 0
            ) {
              currentUser.companyIds = currentUser.companies.map((company: { id: number }) => [
                company.id,
              ]);
            } else {
              currentUser.companyIds = [['0']];
            }
            setSystemUserState(currentUser);
            formRef?.current?.setFieldsValue(currentUser);
          }
        }}
      >
        {' '}
        <UserForm
          companySelectList={companySelectState}
          setCompanySelectList={setCompanySelectState}
          isCreate={false}
        />{' '}
      </ModalForm>{' '}
    </>
  );
};

export default AvatarDropdown;
