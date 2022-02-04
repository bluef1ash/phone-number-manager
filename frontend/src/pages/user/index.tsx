import React, { useRef } from 'react';
import DataList from '@/components/DataList';
import MainPageContainer from '@/components/MainPageContainer';
import type { ActionType } from '@ant-design/pro-table';
import type { ProFormInstance } from '@ant-design/pro-form';
import {
  ProFormList,
  ProFormRadio,
  ProFormSwitch,
  ProFormText,
  ProFormTreeSelect,
} from '@ant-design/pro-form';
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
import { queryCompanyList } from '@/services/company/api';

const CompanyTreeSelect = (isRules: boolean) => {
  const rules = [];
  let label = '';
  if (isRules) {
    rules.push({
      required: true,
      message: '至少选择一个所属单位！',
    });
    label = '系统用户所属单位';
  }
  return (
    <ProFormTreeSelect
      name="companies"
      label={label}
      placeholder="请选择"
      allowClear
      secondary
      request={async () => {
        const { code, data } = await queryCompanyList({ current: 1, pageSize: 10000 });
        return code === 200 ? data.records : [];
      }}
      fieldProps={{
        showArrow: false,
        filterTreeNode: true,
        showSearch: true,
        dropdownMatchSelectWidth: false,
        labelInValue: true,
        autoClearSearchValue: true,
        multiple: true,
        treeNodeFilterProp: 'title',
        fieldNames: {
          label: 'title',
        },
      }}
      rules={rules}
    />
  );
};
const InputElement = (
  <>
    <ProFormText
      width="xl"
      name={['phoneNumber', 'phoneNumber']}
      label="系统用户联系方式"
      tooltip="只支持手机号码"
      placeholder="请输入系统用户联系方式"
      rules={[
        {
          required: true,
          message: '系统用户联系方式不能为空！',
        },
        {
          pattern: /(13\d|14[579]|15[^4\D]|17[^49\D]|18\d)\d{8}/,
          message: '系统用户联系方式输入不正确！',
        },
      ]}
    />{' '}
    <ProFormText
      width="xl"
      name="username"
      label="系统用户姓名"
      tooltip="不能超过10个字符"
      placeholder="请输入系统用户姓名"
      rules={[
        {
          required: true,
          message: '系统用户姓名不能为空！',
        },
        {
          max: 10,
          message: '系统用户名称不能超过100个字符！',
        },
      ]}
    />{' '}
    <ProFormText.Password
      width="xl"
      name="password"
      label="系统用户密码"
      tooltip="不能少于6个字符"
      placeholder="请输入系统用户密码"
      rules={[
        {
          required: true,
          message: '系统用户密码不能为空！',
        },
        {
          min: 6,
          message: '系统用户密码不能少于6个字符！',
        },
      ]}
    />{' '}
    <ProFormText.Password
      width="xl"
      name="confirmPassword"
      label="确认系统用户密码"
      tooltip="不能少于6个字符"
      placeholder="请再次输入系统用户密码"
      dependencies={['password']}
      rules={[
        {
          required: true,
          message: '确认系统用户密码不能为空！',
        },
        {
          min: 6,
          message: '系统用户密码不能少于6个字符！',
        },
        ({ getFieldValue }) => ({
          validator: (rules, value) =>
            getFieldValue('password') === value
              ? Promise.resolve()
              : Promise.reject('两次密码输入不一致'),
        }),
      ]}
    />{' '}
    <ProFormList
      name="positions"
      initialValue={[
        {
          position: undefined,
        },
      ]}
      creatorButtonProps={{
        creatorButtonText: '添加系统用户职务',
      }}
    >
      {' '}
      <ProFormText
        width="xl"
        name="position"
        label="系统用户职务"
        tooltip="不能超过10个字符"
        placeholder="请输入系统用户职务"
        rules={[
          {
            max: 10,
            message: '系统用户职务不能超过10个字符！',
          },
        ]}
      />{' '}
    </ProFormList>{' '}
    <ProFormList
      name="titles"
      initialValue={[
        {
          title: undefined,
        },
      ]}
      creatorButtonProps={{
        creatorButtonText: '添加系统用户职称',
      }}
    >
      {' '}
      <ProFormText
        width="xl"
        name="title"
        label="系统用户职称"
        tooltip="不能超过10个字符"
        placeholder="请输入系统用户职称"
        rules={[
          {
            max: 10,
            message: '系统用户职称不能超过10个字符！',
          },
        ]}
      />{' '}
    </ProFormList>{' '}
    <ProFormRadio.Group
      name="isSubcontract"
      label="是否参加社区分包"
      initialValue={false}
      options={[
        {
          label: '是',
          value: true,
        },
        {
          label: '否',
          value: false,
        },
      ]}
    />{' '}
    <ProFormSwitch name="isLocked" label="系统用户是否被锁定" initialValue={false} />{' '}
    <ProFormSwitch name="isEnabled" label="系统用户是否启用" initialValue={true} />{' '}
    {CompanyTreeSelect(true)}
  </>
);

const SystemUser: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const createFormRef = useRef<ProFormInstance<API.SystemUser>>();
  const ModifyFormRef = useRef<ProFormInstance<API.SystemUser>>();
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
                  const { code, message } = await patchSystemUser(systemUser.id as number, {
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
            dataIndex: 'companyNames',
            ellipsis: true,
            sorter: true,
            renderFormItem: () => CompanyTreeSelect(false),
          },
        ]}
        batchRemoveEventHandler={async (data) =>
          (
            await batchSystemUser<number[]>({
              method: 'DELETE',
              data,
            })
          ).code === 200
        }
        createEditModalForm={{
          element: InputElement,
          props: {
            title: '添加系统用户',
          },
          formRef: createFormRef,
          onFinish: async (formData) => (await createSystemUser(formData)).code === 200,
        }}
        modifyEditModalForm={{
          element: InputElement,
          props: {
            title: '编辑系统用户',
          },
          formRef: ModifyFormRef,
          onFinish: async (formData) => (await modifySystemUser(formData)).code === 200,
          onConfirmRemove: async (id) => (await removeSystemUser(id)).code === 200,
          queryData: async (id) => await querySystemUser(id),
        }}
      />{' '}
    </MainPageContainer>
  );
};

export default SystemUser;
