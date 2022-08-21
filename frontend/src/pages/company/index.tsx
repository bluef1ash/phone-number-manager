import DataList from '@/components/DataList';
import MainPageContainer from '@/components/MainPageContainer';
import PhoneNumberList from '@/components/PhoneNumberList';
import SelectCascder from '@/components/SelectCascder';
import {
  batchCompany,
  createCompany,
  modifyCompany,
  queryCompany,
  queryCompanyList,
  queryCompanySelectList,
  removeCompany,
} from '@/services/company/api';
import { querySystemPermissionSelectList } from '@/services/permission/api';
import { getCompanyParentIds, submitPrePhoneNumberHandle } from '@/services/utils';
import type { ProFormInstance } from '@ant-design/pro-form';
import { ProFormList, ProFormSelect, ProFormText } from '@ant-design/pro-form';
import type { ActionType } from '@ant-design/pro-table';
import type { RuleObject, StoreValue } from 'rc-field-form/lib/interface';
import React, { useEffect, useRef, useState } from 'react';

const InputElement = (
  selectListState: API.SelectList[],
  setSelectListState: React.Dispatch<React.SetStateAction<API.SelectList[]>>,
  systemPermissionState: API.SelectList[],
  setSystemPermissionState: React.Dispatch<React.SetStateAction<API.SelectList[]>>,
  modifyId?: number,
) => (
  <>
    <ProFormText
      width="xl"
      name="name"
      label="单位名称"
      tooltip="不能超过20个字符"
      placeholder="请输入单位名称"
      rules={[
        {
          required: true,
          message: '单位名称不能为空！',
        },
        {
          max: 20,
          message: '单位名称不能超过20个字符！',
        },
      ]}
    />{' '}
    <SelectCascder
      width="xl"
      name="parentIdCascder"
      label="上级单位"
      querySelectList={async (value) => (await queryCompanySelectList([value])).data}
      selectState={selectListState}
      setSelectState={setSelectListState}
      rules={[
        {
          required: true,
          message: '请选择上级单位！',
        },
        {
          validator(rule: RuleObject, value: StoreValue) {
            return typeof modifyId !== 'undefined' &&
              modifyId !== 0 &&
              modifyId.toString() === value[value.length - 1]
              ? Promise.reject('上级编号不能为自身！')
              : Promise.resolve();
          },
        },
      ]}
    />{' '}
    <PhoneNumberList
      name="phoneNumbers"
      initialValue={[{ phoneNumber: '' }]}
      creatorButtonText="添加单位联系方式"
      listValidRejectMessage="请添加单位联系方式！"
      phoneNumberFormProps={{
        name: 'phoneNumber',
        label: '单位联系方式',
        placeholder: '请输入单位联系方式',
      }}
    />{' '}
    <ProFormList
      name="companyExtras"
      creatorButtonProps={{
        creatorButtonText: '添加单位额外属性',
      }}
    >
      {' '}
      <ProFormText
        name="title"
        width="xl"
        label="单位额外属性标题"
        placeholder="请输入单位额外属性标题"
        rules={[
          {
            required: true,
            message: '单位额外属性标题不能为空！',
          },
          {
            max: 100,
            message: '单位额外属性标题不能超过100个字符',
          },
        ]}
      />{' '}
      <ProFormText
        width="xl"
        name="description"
        label="单位额外属性描述"
        tooltip="不能超过255个字符"
        placeholder="请输入单位额外属性描述"
        rules={[
          {
            max: 255,
            message: '单位额外属性描述不能超过255个字符！',
          },
        ]}
      />{' '}
      <ProFormText
        width="xl"
        name="name"
        label="单位额外属性变量名称"
        tooltip="不能超过100个字符"
        placeholder="请输入单位额外属性变量名称"
        rules={[
          {
            required: true,
            message: '单位额外属性变量名称不能为空！',
          },
          {
            max: 100,
            message: '单位额外属性变量名称不能超过100个字符！',
          },
        ]}
      />{' '}
      <ProFormText
        width="xl"
        name="content"
        label="单位额外属性变量值"
        placeholder="请输入单位额外属性变量值"
        rules={[
          {
            required: true,
            message: '单位额外属性变量值不能为空！',
          },
          {
            max: 255,
            message: '单位额外属性变量值不能超过255个字符！',
          },
        ]}
      />{' '}
      <ProFormSelect
        width="xl"
        options={[
          {
            value: 0,
            label: '未知类型',
          },
          {
            value: 1,
            label: '布尔类型',
          },
          {
            value: 2,
            label: '字符串类型',
          },
          {
            value: 3,
            label: '数值类型',
          },
          {
            value: 4,
            label: '系统用户类型',
          },
        ]}
        name="fieldType"
        label="单位额外属性字段类型"
        rules={[
          {
            required: true,
            message: '单位额外属性字段类型不能为空！',
          },
        ]}
      />{' '}
    </ProFormList>{' '}
    <SelectCascder
      width="xl"
      name="systemPermissionSelectList"
      label="单位所属权限"
      querySelectList={async (value) => (await querySystemPermissionSelectList([value])).data}
      selectState={systemPermissionState}
      setSelectState={setSystemPermissionState}
      cascaderFieldProps={{
        multiple: true,
      }}
      rules={[
        {
          required: true,
          message: '请选择单位所属权限！',
        },
        {
          validator(rule: RuleObject, value: StoreValue) {
            return value.length > 1 && value.some((item: any[]) => item[0] === 0)
              ? Promise.reject('单位所属权限不能同时选择系统权限和无权限！')
              : Promise.resolve();
          },
        },
      ]}
    />{' '}
  </>
);

const Company: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const formRef = useRef<ProFormInstance<API.Company>>();
  const [selectListState, setSelectListState] = useState<API.SelectList[]>([]);
  const [systemPermissionState, setSystemPermissionState] = useState<API.SelectList[]>([]);
  const [modifyIdState, setModifyIdState] = useState<number | undefined>(undefined);

  useEffect(() => {
    (async () => {
      const list: API.SelectList[] = [
        {
          label: '无',
          title: '无',
          value: '0',
        },
      ];
      const parentIds = getCompanyParentIds();
      setSelectListState([...list, ...(await queryCompanySelectList(parentIds)).data]);
      setSystemPermissionState([...list, ...(await querySystemPermissionSelectList([0])).data]);
    })();
  }, []);

  const submitPreHandler = (formData: API.Company) => {
    formData.phoneNumbers = formData.phoneNumbers?.map((value: { phoneNumber: string }) =>
      submitPrePhoneNumberHandle(value.phoneNumber),
    );
    formData.systemPermissions = formData.systemPermissionSelectList?.map(
      (value: API.SelectList[]) => ({
        id: value[value.length - 1],
      }),
    );
    if (typeof formData.parentIdCascder !== 'undefined') {
      formData.parentId = formData.parentIdCascder[formData.parentIdCascder.length - 1][0];
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
          breadcrumbName: '单位管理',
        },
        {
          path: '/company',
          breadcrumbName: '单位列表',
        },
      ]}
    >
      {' '}
      <DataList<API.Company, API.Company>
        customActionRef={actionRef}
        customRequest={async (params) => await queryCompanyList(params)}
        customColumns={[
          {
            title: '单位名称',
            dataIndex: 'name',
            sorter: true,
            ellipsis: true,
          },
          {
            title: '联系方式',
            dataIndex: 'phoneNumbers',
            ellipsis: true,
            render(dom, entity) {
              return (
                <>
                  {entity.phoneNumbers?.map(
                    (phoneNumber: { phoneNumber: string | number }, index: number) =>
                      phoneNumber.phoneNumber +
                      (index + 1 == entity.phoneNumbers?.length ? '' : '，'),
                  )}
                </>
              );
            },
          },
        ]}
        batchRemoveEventHandler={async (data) =>
          await batchCompany({
            method: 'DELETE',
            data,
          })
        }
        modalForm={{
          title: '单位',
          element: InputElement(
            selectListState,
            setSelectListState,
            systemPermissionState,
            setSystemPermissionState,
            modifyIdState,
          ),
          formRef: formRef,
          onCreateFinish: async (formData) => await createCompany(submitPreHandler(formData)),
          onModifyFinish: async (formData) =>
            await modifyCompany(formData.id, submitPreHandler(formData)),
          queryData: async (id) => {
            const result = await queryCompany(id);
            if (
              result.data.systemPermissions === null ||
              result.data.systemPermissions.length === 0
            ) {
              result.data.systemPermissionSelectList = [['0']];
            } else {
              result.data.systemPermissionSelectList = result.data.systemPermissions?.map(
                (systemPermission: { id: number; name: string }) => [systemPermission.id],
              );
            }
            if (typeof result.data.parentId !== 'undefined') {
              result.data.parentIdCascder = [[result.data.parentId]];
            }
            setModifyIdState(id);
            return result;
          },
          modifyButtonPreHandler() {
            setModifyIdState(undefined);
          },
        }}
        removeData={async (id) => await removeCompany(id)}
      />{' '}
    </MainPageContainer>
  );
};

export default Company;
