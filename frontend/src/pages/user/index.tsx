import DataList from '@/components/DataList';
import MainPageContainer from '@/components/MainPageContainer';
import SelectCascder from '@/components/SelectCascder';
import UserForm, { UserFormSubmitPreHandler } from '@/components/UserForm';
import { queryCompanySelectList } from '@/services/company/api';
import {
  batchSystemUser,
  createSystemUser,
  modifySystemUser,
  patchSystemUser,
  querySystemUser,
  querySystemUserList,
  removeSystemUser,
} from '@/services/user/api';
import { getCompanyParentIds } from '@/services/utils';
import { useModel } from '@@/plugin-model/useModel';
import type { ProFormInstance } from '@ant-design/pro-form';
import type { ActionType } from '@ant-design/pro-table';
import { message as msg, Switch } from 'antd';
import React, { useEffect, useRef, useState } from 'react';

const SystemUser: React.FC = () => {
  const { initialState } = useModel('@@initialState');
  const actionRef = useRef<ActionType>();
  const formRef = useRef<ProFormInstance<API.SystemUser>>();
  const [companySelectState, setCompanySelectState] = useState<API.SelectList[]>([]);

  useEffect(() => {
    (async () =>
      setCompanySelectState([
        {
          label: '无',
          title: '无',
          value: '0',
        },
        ...(await queryCompanySelectList(getCompanyParentIds(initialState?.currentUser))).data,
      ]))();
  }, []);

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
            render(text, record) {
              if (record.loginTime === '1000-01-01 00:00:00') {
                return '未登录';
              }
              return record.loginTime;
            },
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
            render(text, record) {
              return (
                <Switch
                  checked={record.isLocked}
                  onChange={async (checked) => {
                    const { code, message } = await patchSystemUser(record.id, {
                      id: record.id,
                      isLocked: checked,
                    });
                    if (code === 200) {
                      record.isLocked = checked;
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
              );
            },
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
                name = record.companies.map((company: { name: string }) => company.name).join('，');
              }
              return name;
            },
            renderFormItem() {
              return (
                <SelectCascder
                  querySelectList={async (value) => (await queryCompanySelectList([value])).data}
                  selectState={companySelectState}
                  setSelectState={setCompanySelectState}
                  cascaderFieldProps={{
                    multiple: true,
                  }}
                />
              );
            },
          },
        ]}
        batchRemoveEventHandler={async (data) =>
          await batchSystemUser<number[]>({
            method: 'DELETE',
            data,
          })
        }
        modalForm={{
          title: '系统用户',
          element: (
            <UserForm
              companySelectList={companySelectState}
              setCompanySelectList={setCompanySelectState}
              isCreate={true}
            />
          ),
          formRef: formRef,
          onCreateFinish: async (formData) =>
            await createSystemUser(UserFormSubmitPreHandler(formData)),
          onModifyFinish: async (formData) =>
            await modifySystemUser(formData.id, UserFormSubmitPreHandler(formData)),
          queryData: async (id) => {
            const result = await querySystemUser(id);
            if (
              typeof result.data.companies !== 'undefined' &&
              result.data.companies !== null &&
              result.data.companies.length > 0
            ) {
              result.data.companyIds = result.data.companies.map((company: { id: number }) => [
                company.id,
              ]);
            } else {
              result.data.companyIds = [['0']];
            }
            return result;
          },
        }}
        removeData={async (id) => await removeSystemUser(id)}
      />{' '}
    </MainPageContainer>
  );
};

export default SystemUser;
