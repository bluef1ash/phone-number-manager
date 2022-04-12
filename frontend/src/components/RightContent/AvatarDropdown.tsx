import { account } from "@/services/api";
import { logout } from "@/services/account/api";
import { LogoutOutlined, SettingOutlined, UserOutlined } from "@ant-design/icons";
import { SESSION_COMPONENTS_KEY, SESSION_MENU_KEY, SESSION_SYSTEM_USER_KEY, SESSION_TOKEN_KEY } from "@config/constant";
import { Avatar, Menu, message as msg, Spin } from "antd";
import { stringify } from "querystring";
import React, { useRef, useState } from "react";
import { history, useModel } from "umi";
import HeaderDropdown from "../HeaderDropdown";
import styles from "./index.less";
import EditModalForm from "@/components/DataList/EditModalForm";
import type { ProFormInstance } from "@ant-design/pro-form";
import UserForm, { UserFormSubmitPreHandler } from "@/components/UserForm";
import { queryCompanySelectList } from "@/services/company/api";
import { modifySystemUser, queryCurrentUser } from "@/services/user/api";
import { getCompanyParentIds } from "@/services/utils";

//eslint-disable-next-line @typescript-eslint/ban-types
export type GlobalHeaderRightProps = {};

const AvatarDropdown: React.FC<GlobalHeaderRightProps> = () => {
  const { initialState } = useModel('@@initialState');
  const formRef = useRef<ProFormInstance<API.SystemUser>>();
  const [companySelectState, setCompanySelectState] = useState<API.SelectList[]>([]);
  const [systemUserState, setSystemUserState] = useState<API.SystemUser>();

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
        //@ts-ignore
        const loginUri = account.login.substring((REACT_APP_API_BASE_URL as string).length);
        switch (key) {
          case 'logout':
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
            history.push(loginUri);
            break;
          case 'restPassword':
            break;
          default:
            break;
        }
      }}
    >
      {' '}
      <Menu.Item key="restPassword">
        {' '}
        <EditModalForm<API.SystemUser>
          formRef={formRef}
          width={600}
          trigger={
            <span>
              <SettingOutlined /> 修改密码{' '}
            </span>
          }
          submitter={{
            searchConfig: {
              submitText: '保存',
              resetText: '重置',
            },
            resetButtonProps: {
              onClick: () => formRef?.current?.resetFields(),
            },
          }}
          onFinish={async (formData) => {
            formData.id = systemUserState?.id as number;
            const { code, message } = await modifySystemUser(
              formData.id,
              UserFormSubmitPreHandler(formData),
            );
            if (code === 200) {
              const { data } = await queryCurrentUser();
              localStorage.setItem(SESSION_SYSTEM_USER_KEY, JSON.stringify(data));
              msg.success(message === 'success' ? '修改成功！' : message);
              return true;
            }
            msg.error(message);
            return false;
          }}
          onVisibleChange={async (visible) => {
            if (visible) {
              const parentIds = getCompanyParentIds();
              setCompanySelectState((await queryCompanySelectList(parentIds)).data);
              const systemUser: API.SystemUser = JSON.parse(
                localStorage.getItem(SESSION_SYSTEM_USER_KEY) as string,
              );
              systemUser.phoneNumber = {
                //@ts-ignore
                phoneNumber: systemUser.phoneNumber,
              };
              setSystemUserState(systemUser);
              formRef?.current?.setFieldsValue(systemUser);
            }
          }}
        >
          {' '}
          <UserForm companySelectList={companySelectState}  setCompanySelectList={setCompanySelectState}/>{' '}
        </EditModalForm>{' '}
      </Menu.Item>{' '}
      <Menu.Divider />{' '}
      <Menu.Item key="logout">
        {' '}
        <LogoutOutlined /> 退出登录{' '}
      </Menu.Item>{' '}
    </Menu>
  );
  return (
    <HeaderDropdown overlay={menuHeaderDropdown}>
      <span className={`${styles.action} ${styles.account}`}>
        <Avatar size="small" className={styles.avatar}  icon={<UserOutlined />} />
        <span className={`${styles.name} anticon`}>{initialState.currentUser.username}</span>
      </span>
    </HeaderDropdown>
  );
};

export default AvatarDropdown;
