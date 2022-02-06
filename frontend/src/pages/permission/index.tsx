import type { MutableRefObject } from 'react';
import React, { useEffect, useRef, useState } from 'react';
import DataList from '@/components/DataList';
import MainPageContainer from '@/components/MainPageContainer';
import type { ActionType } from '@ant-design/pro-table';
import type { ProFormInstance } from '@ant-design/pro-form';
import {
  ProFormList,
  ProFormSelect,
  ProFormSwitch,
  ProFormText,
  ProFormTreeSelect,
} from '@ant-design/pro-form';
import {
  batchSystemPermission,
  createSystemPermission,
  modifySystemPermission,
  querySystemPermission,
  querySystemPermissionList,
  querySystemPermissionSelectList,
  removeSystemPermission,
} from '@/services/permission/api';
import type { RuleObject, StoreValue } from 'rc-field-form/lib/interface';

const InputElement = (
  SystemPermissions: API.SelectList[],
  setSystemPermissionIdState: { (value: React.SetStateAction<number | undefined>): void },
  formRef: MutableRefObject<ProFormInstance<API.SystemPermission> | undefined>,
) => (
  <>
    <ProFormText
      width="xl"
      name="name"
      label="系统权限名称"
      tooltip="不能超过30个字符"
      placeholder="请输入系统权限名称"
      rules={[
        {
          required: true,
          message: '系统权限名称不能为空！',
        },
        {
          max: 30,
          message: '系统权限名称不能超过30个字符！',
        },
      ]}
    />{' '}
    <ProFormText
      width="xl"
      name="functionName"
      label="系统权限约束名称"
      tooltip="只能输入英文和数字，不能超过255个字符"
      placeholder="请输入系统权限约束名称"
      rules={[
        {
          required: true,
          message: '系统权限约束名称不能为空！',
        },
        {
          max: 255,
          message: '系统权限约束名称不能超过255个字符！',
        },
        {
          pattern: /^\w+$/g,
          message: '系统权限约束名称只能输入英文和数字！',
        },
      ]}
    />{' '}
    <ProFormText
      width="xl"
      name="uri"
      label="系统权限地址"
      tooltip="不能超过255个字符"
      placeholder="请输入系统权限地址"
      rules={[
        {
          max: 255,
          message: '系统权限地址不能超过255个字符！',
        },
        {
          pattern: /^\/[\w\-]+$/g,
          message: '系统权限地址不正确！',
        },
        {
          validator(rule: RuleObject, value: StoreValue) {
            const httpMethods = formRef?.current?.getFieldValue('httpMethods');
            return typeof httpMethods !== 'undefined' &&
              httpMethods.length > 0 &&
              (typeof value === 'undefined' || value.length == 0)
              ? Promise.reject('系统权限地址不能为空！')
              : Promise.resolve();
          },
        },
      ]}
    />{' '}
    <ProFormList
      name="httpMethods"
      creatorButtonProps={{
        creatorButtonText: '添加系统权限方式',
      }}
      rules={[
        {
          validator(rule: RuleObject, value: StoreValue) {
            const uri = formRef?.current?.getFieldValue('uri');
            let flag = false;
            if (Array.isArray(value) && value.length > 0) {
              for (let i = 0; i < value.length; i++) {
                if (typeof value[i]?.httpMethod === 'undefined') {
                  flag = true;
                  break;
                }
                flag = false;
              }
            } else {
              flag = true;
            }
            return typeof uri !== 'undefined' && uri.length > 0 && flag
              ? Promise.reject('请选择系统权限方式！')
              : Promise.resolve();
          },
        },
      ]}
    >
      {' '}
      <ProFormSelect
        name="httpMethod"
        width="xl"
        label="系统权限方式"
        valueEnum={{
          GET: 'GET',
          HEAD: 'HEAD',
          POST: 'POST',
          PUT: 'PUT',
          PATCH: 'PATCH',
          DELETE: 'DELETE',
          OPTIONS: 'OPTIONS',
          TRACE: 'TRACE',
        }}
        placeholder="请选择"
      />{' '}
    </ProFormList>
    <ProFormTreeSelect
      width="xl"
      name="parentId"
      label="系统权限上级编号"
      request={async () => SystemPermissions}
      placeholder="请选择"
      rules={[
        {
          required: true,
          message: '请选择系统权限上级编号！',
        },
      ]}
      fieldProps={{
        onChange: (value: number) => setSystemPermissionIdState(value),
      }}
    />{' '}
    <ProFormSelect
      width="xl"
      name="menuType"
      label="系统权限菜单类型"
      valueEnum={{
        ALL: '全部',
        FRONTEND: '前端',
        BACKEND: '后端',
      }}
      placeholder="请选择"
      rules={[
        {
          required: true,
          message: '请选择系统权限菜单类型！',
        },
      ]}
    />{' '}
    <ProFormSwitch name="isDisplay" label="系统权限是否显示" initialValue={false} />{' '}
  </>
);

const SystemPermission: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const createFormRef = useRef<ProFormInstance<API.SystemPermission>>();
  const modifyFormRef = useRef<ProFormInstance<API.SystemPermission>>();
  const [systemPermissionState, setSystemPermissionState] = useState<API.SelectList[]>();
  const [systemPermissionIdState, setSystemPermissionIdState] = useState<number | undefined>(
    undefined,
  );
  useEffect(() => {
    const queryPermissions = async () => (await querySystemPermissionSelectList())?.data;
    queryPermissions().then((result) => setSystemPermissionState(result as API.SelectList[]));
  }, []);

  const submitPreHandler = (formData: API.SystemPermission) => {
    if (
      typeof systemPermissionIdState !== 'undefined' &&
      typeof systemPermissionState !== 'undefined'
    ) {
      const select = systemPermissionState.find(({ value }) => value === systemPermissionIdState);
      formData.level = (select?.level as number) + 1;
    }
    if (Array.isArray(formData.httpMethods) && formData.httpMethods.length > 0) {
      formData.httpMethods = formData.httpMethods.map(
        (value) => (value as { httpMethod: API.HttpMethod }).httpMethod,
      );
    }
    return formData;
  };

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
          path: '/system/permission',
          breadcrumbName: '系统权限列表',
        },
      ]}
    >
      {' '}
      <DataList<API.SystemPermission, API.SystemPermission>
        customActionRef={actionRef}
        customRequest={async (params) => await querySystemPermissionList(params)}
        customColumns={[
          {
            title: '系统权限名称',
            dataIndex: 'name',
            sorter: true,
            ellipsis: true,
          },
          {
            title: '系统权限约束名称',
            dataIndex: 'functionName',
            sorter: true,
            ellipsis: true,
          },
          {
            title: '系统权限方式',
            dataIndex: 'httpMethods',
            sorter: true,
            ellipsis: true,
            valueEnum: {
              GET: { text: 'GET' },
              HEAD: { text: 'HEAD' },
              POST: { text: 'POST' },
              PUT: { text: 'PUT' },
              PATCH: { text: 'PATCH' },
              DELETE: { text: 'DELETE' },
              OPTIONS: { text: 'OPTIONS' },
              TRACE: { text: 'TRACE' },
            },
          },
          {
            title: '系统权限地址',
            dataIndex: 'uri',
            sorter: true,
            ellipsis: true,
          },
        ]}
        batchRemoveEventHandler={async (data) =>
          await batchSystemPermission<number[]>({
            method: 'DELETE',
            data,
          })
        }
        createEditModalForm={{
          element: InputElement(
            systemPermissionState as API.SelectList[],
            setSystemPermissionIdState,
            createFormRef,
          ),
          props: {
            title: '添加系统权限',
          },
          formRef: createFormRef,
          onFinish: async (formData) => await createSystemPermission(submitPreHandler(formData)),
        }}
        modifyEditModalForm={{
          element: InputElement(
            systemPermissionState as API.SystemPermission[],
            setSystemPermissionIdState,
            modifyFormRef,
          ),
          props: {
            title: '编辑系统权限',
          },
          formRef: modifyFormRef,
          onFinish: async (formData) => await modifySystemPermission(submitPreHandler(formData)),
          onConfirmRemove: async (id) => await removeSystemPermission(id),
          queryData: async (id) => {
            const result = await querySystemPermission(id);
            if (Array.isArray(result.data.httpMethods)) {
              result.data.httpMethods = result.data.httpMethods.map((httpMethod) => ({
                httpMethod,
              })) as { httpMethod: API.HttpMethod }[];
            }
            return result;
          },
        }}
      />{' '}
    </MainPageContainer>
  );
};

export default SystemPermission;
