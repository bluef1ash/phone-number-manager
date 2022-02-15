import React, { useRef, useState } from 'react';
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
import { isArray } from 'lodash';
import { parsePhoneNumber } from 'libphonenumber-js/max';

const InputElement = (
  selectListState: API.SelectList[],
  setCompanyIdState: { (value: React.SetStateAction<number | undefined>): void },
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
      max={10000}
      fieldProps={{ precision: 0 }}
    />{' '}
    <ProFormTreeSelect
      width="xl"
      name="parentId"
      label="上级单位"
      request={async () => selectListState}
      placeholder="请选择"
      rules={[
        {
          required: true,
          message: '请选择上级单位！',
        },
      ]}
      fieldProps={{
        onChange: (value: number) => setCompanyIdState(value),
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
            return typeof value !== 'undefined' && isArray(value)
              ? Promise.resolve()
              : Promise.reject('请添加联系方式！');
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
            validator(role: RuleObject, value: StoreValue) {
              try {
                return typeof value !== 'undefined' &&
                  value.length > 0 &&
                  parsePhoneNumber(value, 'CN').isValid()
                  ? Promise.resolve()
                  : Promise.reject('联系方式输入不正确！');
              } catch (e) {
                return Promise.reject('联系方式输入不正确！');
              }
            },
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
  const [selectListState, setSelectListState] = useState<API.SelectList[]>();
  const [companyIdState, setCompanyIdState] = useState<number>();

  const submitPreHandler = (formData: API.Company) => {
    if (typeof companyIdState !== 'undefined' && isArray(selectListState)) {
      const select = selectListState.find(({ value }) => value === companyIdState);
      formData.level = (select?.level as number) + 1;
    }
    formData.phoneNumbers = formData.phoneNumbers?.map((value) => {
      const phoneType = parsePhoneNumber(value.phoneNumber as string, 'CN').getType();
      let pt = '';
      if (typeof phoneType !== 'undefined') {
        pt = phoneType.toString();
      }
      return {
        phoneType: pt,
        phoneNumber: value.phoneNumber,
      };
    });
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
            sorter: true,
            ellipsis: true,
            render(dom, entity) {
              return (
                <>
                  {entity.phoneNumbers?.map(
                    (phoneNumber, index) =>
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
        createEditModalForm={{
          element: InputElement(selectListState as API.SelectList[], setCompanyIdState),
          props: {
            title: '添加单位',
          },
          formRef: createFormRef,
          onFinish: async (formData) => await createCompany(submitPreHandler(formData)),
        }}
        modifyEditModalForm={{
          element: InputElement(selectListState as API.SelectList[], setCompanyIdState),
          props: {
            title: '编辑单位',
          },
          formRef: modifyFormRef,
          onFinish: async (formData) => await modifyCompany(submitPreHandler(formData)),
          onConfirmRemove: async (id) => await removeCompany(id),
          queryData: async (id) => await queryCompany(id),
        }}
        onLoad={async () => {
          let list: API.SelectList[] = [
            {
              title: '无',
              value: 0,
              level: 0,
            },
          ];
          const companySelect = (await queryCompanySelectList()).data;
          if (companySelect.length > 0) {
            list = [...list, ...companySelect];
          }
          setSelectListState(list);
        }}
      />{' '}
    </MainPageContainer>
  );
};

export default Company;
