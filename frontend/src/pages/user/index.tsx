import React, { useRef, useState } from 'react';
import DataList from '@/components/DataList';
import MainPageContainer from '@/components/MainPageContainer';
import type { ActionType } from '@ant-design/pro-table';
import type { ProFormInstance } from '@ant-design/pro-form';
import {
  batchSystemUser,
  createSystemUser,
  modifySystemUser,
  patchSystemUser,
  querySystemUser,
  querySystemUserList,
  removeSystemUser,
} from '@/services/user/api';
import { message as msg, Switch } from 'antd';
import { queryCompanySelectList } from '@/services/company/api';
import UserForm, { UserFormSubmitPreHandler } from '@/components/UserForm';
import CompanyTreeSelect from '@/components/UserForm/CompanyTreeSelect';

const SystemUser: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const createFormRef = useRef<ProFormInstance<API.SystemUser>>();
  const ModifyFormRef = useRef<ProFormInstance<API.SystemUser>>();
  const [companySelectState, setCompanySelectState] = useState<API.SelectList[]>([]);

  return (
    <MainPageContainer
      routes={[
        {
          path: '/welcome',
          breadcrumbName: '首页',
        },
        {
          path: '',
          breadcrumbName: '系统管理',
        },
        {
          path: '/system/user',
          breadcrumbName: '系统用户列表',
        },
      ]}
    >
      {' '}
      <DataList<API.SystemUser, API.SystemUser>
        customActionRef={actionRef}
        customRequest={async (params) => await querySystemUserList(params)}
        customColumns={[
          {
            title: '系统用户名称',
            dataIndex: 'username',
            sorter: true,
            ellipsis: true,
          },
          {
            title: '系统用户登录时间',
            dataIndex: 'loginTime',
            sorter: true,
            ellipsis: true,
            search: false,
          },
          {
            title: '系统用户登录IP地址',
            dataIndex: 'loginIp',
            sorter: true,
            ellipsis: true,
          },
          {
            title: '系统用户是否被锁定',
            dataIndex: 'isLocked',
            sorter: true,
            ellipsis: true,
            render: (dom, systemUser) => (
              <Switch
                checked={systemUser.isLocked}
                onChange={async (checked) => {
                  const { code, message } = await patchSystemUser(systemUser.id as number, {
                    id: systemUser.id,
                    isLocked: checked,
                  });
                  if (code === 200) {
                    systemUser.isLocked = checked;
                    let lockedString = '解锁';
                    if (checked) {
                      lockedString = '锁定';
                    }
                    msg.success(lockedString + '成功！');
                  } else {
                    msg.error(message);
                  }
                }}
              />
            ),
            search: false,
          },
          {
            title: '系统用户是否启用',
            dataIndex: 'isEnabled',
            sorter: true,
            ellipsis: true,
            render: (dom, systemUser) => (
              <Switch
                checked={systemUser.isEnabled}
                onChange={async (checked) => {
                  const { code, message } = await patchSystemUser(systemUser.id, {
                    id: systemUser.id,
                    isEnabled: checked,
                  });
                  if (code === 200) {
                    let enabledString = '禁用';
                    if (checked) {
                      enabledString = '启用';
                    }
                    systemUser.isEnabled = checked;
                    msg.success(enabledString + '成功！');
                  } else {
                    msg.error(message);
                  }
                }}
              />
            ),
            search: false,
          },
          {
            title: '系统用户所属单位',
            dataIndex: 'companies',
            ellipsis: true,
            sorter: true,
            render(value, record) {
              let name = '无';
              if (
                record.companies !== null &&
                typeof record.companies !== 'undefined' &&
                record.companies.length > 0
              ) {
                name = record.companies.map((company) => company.name).join('，');
              }
              return name;
            },
            renderFormItem() {
              return <CompanyTreeSelect isRules={false} companySelectList={companySelectState} />;
            },
          },
        ]}
        batchRemoveEventHandler={async (data) =>
          await batchSystemUser<number[]>({
            method: 'DELETE',
            data,
          })
        }
        createEditModalForm={{
          element: <UserForm companySelectList={companySelectState} />,
          props: {
            title: '添加系统用户',
          },
          formRef: createFormRef,
          onFinish: async (formData) => await createSystemUser(UserFormSubmitPreHandler(formData)),
        }}
        modifyEditModalForm={{
          element: <UserForm companySelectList={companySelectState} />,
          props: {
            title: '编辑系统用户',
          },
          formRef: ModifyFormRef,
          onFinish: async (formData) =>
            await modifySystemUser(formData.id, UserFormSubmitPreHandler(formData)),
          onConfirmRemove: async (id) => await removeSystemUser(id),
          queryData: async (id) => {
            const result = await querySystemUser(id);
            if (result.code === 200 && result.data.companies !== null) {
              const companies = result.data.companies;
              result.data.companyIds = companies?.map((company) => company.id);
              if (typeof result.data.positions !== 'undefined' && result.data.positions !== null) {
                //@ts-ignore
                result.data.positions = result.data.positions.map((position) => ({ position }));
              }
              if (typeof result.data.titles !== 'undefined' && result.data.titles !== null) {
                //@ts-ignore
                result.data.titles = result.data.titles.map((title) => ({ title }));
              }
            }
            return result;
          },
        }}
        onLoad={async () => setCompanySelectState((await queryCompanySelectList()).data)}
      />{' '}
    </MainPageContainer>
  );
};

export default SystemUser;
