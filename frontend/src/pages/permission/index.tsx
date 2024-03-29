import React, { useEffect, useRef, useState } from 'react';
import DataList from '@/components/DataList';
import MainPageContainer from '@/components/MainPageContainer';
import type { ActionType } from '@ant-design/pro-table';
import type { ProFormInstance } from '@ant-design/pro-form';
import { ProFormList, ProFormSelect, ProFormSwitch, ProFormText } from '@ant-design/pro-form';
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
import SelectCascder from '@/components/SelectCascder';
import type { HttpMethod } from '@/services/enums';

const InputElement = (
  selectListState: API.SelectList[],
  setSelectListState: React.Dispatch<React.SetStateAction<API.SelectList[]>>,
  formRef: React.MutableRefObject<ProFormInstance<API.SystemPermission> | undefined>,
  modifyId?: number,
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
              httpMethods !== null &&
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
          0: 'GET',
          1: 'HEAD',
          2: 'POST',
          3: 'PUT',
          4: 'PATCH',
          5: 'DELETE',
          6: 'OPTIONS',
          7: 'TRACE',
        }}
      />{' '}
    </ProFormList>{' '}
    <SelectCascder
      width="xl"
      name="parentIdCascder"
      label="系统权限上级权限"
      querySelectList={async (value) => (await querySystemPermissionSelectList([value])).data}
      selectState={selectListState}
      setSelectState={setSelectListState}
      rules={[
        {
          required: true,
          message: '请选择系统权限上级权限！',
        },
        {
          validator(rule: RuleObject, value: StoreValue) {
            return typeof modifyId !== 'undefined' &&
              modifyId !== 0 &&
              modifyId.toString() === value[value.length - 1]
              ? Promise.reject('上级权限不能为自身！')
              : Promise.resolve();
          },
        },
      ]}
    />{' '}
    <ProFormSelect
      width="xl"
      name="menuType"
      label="系统权限菜单类型"
      valueEnum={{
        0: '全部',
        1: '前端',
        2: '后端',
      }}
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
  const formRef = useRef<ProFormInstance<API.SystemPermission>>();
  const [selectListState, setSelectListState] = useState<API.SelectList[]>([]);
  const [modifyIdState, setModifyIdState] = useState<number | undefined>(undefined);

  useEffect(() => {
    (async () => {
      let list: API.SelectList[] = [
        {
          label: '顶级权限',
          value: '0',
          level: 0,
        },
      ];
      const data = (await querySystemPermissionSelectList([0]))?.data;
      if (data && data.length > 0) {
        list = [...list, ...data];
      }
      setSelectListState(list);
    })();
  }, []);

  const submitPreHandler = (formData: API.SystemPermission) => {
    if (Array.isArray(formData.httpMethods) && formData.httpMethods.length > 0) {
      formData.httpMethods = formData.httpMethods.map(
        (value: { httpMethod: HttpMethod }) => value.httpMethod,
      );
    }
    if (typeof formData.parentIdCascder !== 'undefined') {
      formData.parentId = formData.parentIdCascder[formData.parentIdCascder.length - 1];
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
        modalForm={{
          title: '系统权限',
          element: InputElement(selectListState, setSelectListState, formRef, modifyIdState),
          formRef: formRef,
          onCreateFinish: async (formData) =>
            await createSystemPermission(submitPreHandler(formData)),
          onModifyFinish: async (formData) =>
            await modifySystemPermission(formData.id, submitPreHandler(formData)),
          queryData: async (id) => {
            const result = await querySystemPermission(id);
            if (Array.isArray(result.data.httpMethods)) {
              result.data.httpMethods = result.data.httpMethods.map(
                (httpMethod: { httpMethod: HttpMethod }) => ({
                  httpMethod,
                }),
              );
            }
            if (typeof result.data.parentId !== 'undefined') {
              result.data.parentIdCascder = [[result.data.parentId]];
            }
            result.data.menuType = result.data.menuType.toString();
            setModifyIdState(id);
            return result;
          },
          modifyButtonPreHandler() {
            setModifyIdState(undefined);
          },
        }}
        removeData={async (id) => await removeSystemPermission(id)}
      />{' '}
    </MainPageContainer>
  );
};

export default SystemPermission;
