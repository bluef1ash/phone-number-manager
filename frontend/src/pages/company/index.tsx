import React, { useEffect, useRef, useState } from 'react';
import DataList from '@/components/DataList';
import MainPageContainer from '@/components/MainPageContainer';
import type { ActionType } from '@ant-design/pro-table';
import type { ProFormInstance } from '@ant-design/pro-form';
import { ProFormDigit, ProFormList, ProFormText, ProFormTreeSelect } from '@ant-design/pro-form';
import type { RuleObject, StoreValue } from 'rc-field-form/lib/interface';
import {
  batchCompany,
  createCompany,
  modifyCompany,
  queryCompany,
  queryCompanyList,
  queryCompanySelectList,
  removeCompany,
} from '@/services/company/api';

const InputElement = (
  companySelect: API.SelectList[],
  setSystemPermissionIdState: { (value: React.SetStateAction<number | undefined>): void },
) => (
  <>
    <ProFormText
      width="xl"
      name="name"
      label="单位名称"
      tooltip="不能超过10个字符"
      placeholder="请输入单位名称"
      rules={[
        {
          required: true,
          message: '单位名称不能为空！',
        },
        {
          max: 10,
          message: '单位名称不能超过10个字符！',
        },
      ]}
    />{' '}
    <ProFormDigit
      width="xl"
      name="actualNumber"
      label="单位分包数"
      tooltip="只能输入数字"
      placeholder="请输入单位分包数"
      max={1000}
      fieldProps={{ precision: 0 }}
    />{' '}
    <ProFormTreeSelect
      width="xl"
      name="parentId"
      label="单位上级编号"
      request={async () => companySelect}
      placeholder="请选择"
      rules={[
        {
          required: true,
          message: '请选择单位上级编号！',
        },
      ]}
      fieldProps={{
        onChange: (value: number) => setSystemPermissionIdState(value),
      }}
    />{' '}
    <ProFormList
      name="phoneNumbers"
      creatorButtonProps={{
        creatorButtonText: '添加联系方式',
      }}
      rules={[
        {
          validator(rule: RuleObject, value: StoreValue) {
            return typeof value !== 'undefined' && value.length > 0
              ? Promise.reject('请添加联系方式！')
              : Promise.resolve();
          },
        },
      ]}
    >
      {' '}
      <ProFormText
        name="phoneNumber"
        width="xl"
        label="联系方式"
        placeholder="请输入联系方式"
        rules={[
          {
            required: true,
            message: '联系方式不能为空！',
          },
          {
            pattern:
              /^((0\d{2,3}-)?\d{7,8}(-\d{1,6})?)|((13\d|14[579]|15[^4\D]|17[^49\D]|18\d)\d{8})$/g,
            message: '联系方式输入不正确！',
          },
        ]}
      />{' '}
    </ProFormList>
  </>
);

const Company: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const createFormRef = useRef<ProFormInstance<API.Company>>();
  const modifyFormRef = useRef<ProFormInstance<API.Company>>();
  const [companyState, setCompanyState] = useState<API.SelectList[]>();
  const [companyIdState, setCompanyIdState] = useState<number | undefined>(undefined);
  useEffect(() => {
    const companies = async () => {
      let list: API.SelectList[] = [
        {
          title: '无',
          value: 0,
          level: 0,
        },
      ];
      //@ts-ignore
      const companySelect = (await queryCompanySelectList()).data as API.SelectList[];
      if (companySelect.length > 0) {
        list = [...list, ...companySelect];
      }
      return list;
    };
    companies().then((result) => setCompanyState(result as API.SelectList[]));
  }, []);

  const submitPreHandler = (formData: API.Company) => {
    if (typeof companyIdState !== 'undefined' && !companyState) {
      //@ts-ignore
      const select = companyState.find(({ value }) => value === companyIdState);
      formData.level = (select?.level as number) + 1;
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
            dataIndex: 'phoneNumber',
            sorter: true,
            ellipsis: true,
          },
        ]}
        batchRemoveEventHandler={async (data) =>
          await batchCompany<number[]>({
            method: 'DELETE',
            data,
          })
        }
        createEditModalForm={{
          element: InputElement(companyState as API.SelectList[], setCompanyIdState),
          props: {
            title: '添加单位',
          },
          formRef: createFormRef,
          onFinish: async (formData) => await createCompany(submitPreHandler(formData)),
        }}
        modifyEditModalForm={{
          element: InputElement(companyState as API.SelectList[], setCompanyIdState),
          props: {
            title: '编辑单位',
          },
          formRef: modifyFormRef,
          onFinish: async (formData) => await modifyCompany(submitPreHandler(formData)),
          onConfirmRemove: async (id) => await removeCompany(id),
          queryData: async (id) => {
            const result = await queryCompany(id);
            if (Array.isArray(result.data.phoneNumbers)) {
              result.data.phoneNumbers = result.data.phoneNumbers.map((phoneNumber) => ({
                phoneNumber,
              })) as { phoneNumber: API.PhoneNumber }[];
            }
            return result;
          },
        }}
      />{' '}
    </MainPageContainer>
  );
};

export default Company;
