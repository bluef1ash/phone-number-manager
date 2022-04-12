import React, { useRef, useState } from 'react';
import DataList from '@/components/DataList';
import MainPageContainer from '@/components/MainPageContainer';
import type { ActionType } from '@ant-design/pro-table';
import type { ProFormInstance } from '@ant-design/pro-form';
import { ProFormList, ProFormSelect, ProFormText } from '@ant-design/pro-form';
import {
  batchCompany,
  createCompany,
  modifyCompany,
  queryCompany,
  queryCompanyList,
  queryCompanySelectList,
  removeCompany,
} from '@/services/company/api';
import { getCompanyParentIds, submitPrePhoneNumberHandle } from '@/services/utils';
import { querySystemPermissionSelectList } from '@/services/permission/api';
import PhoneNumberList from '@/components/PhoneNumberList';
import SelectCascder from '@/components/SelectCascder';

const InputElement = (
  selectListState: API.SelectList[],
  setSelectListState: React.Dispatch<React.SetStateAction<API.SelectList[]>>,
  systemPermissionState: API.SelectList[],
  setSystemPermissionState: React.Dispatch<React.SetStateAction<API.SelectList[]>>,
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
      name="parentId"
      label="上级单位"
      querySelectList={async (value) => (await queryCompanySelectList([value])).data}
      selectState={selectListState}
      setSelectState={setSelectListState}
      rules={[
        {
          required: true,
          message: '请选择上级单位！',
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
    />{' '}
  </>
);

const Company: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const createFormRef = useRef<ProFormInstance<API.Company>>();
  const modifyFormRef = useRef<ProFormInstance<API.Company>>();
  const [selectListState, setSelectListState] = useState<API.SelectList[]>([]);
  const [systemPermissionState, setSystemPermissionState] = useState<API.SelectList[]>([]);

  const submitPreHandler = (formData: API.Company) => {
    formData.phoneNumbers = formData.phoneNumbers?.map((value) =>
      submitPrePhoneNumberHandle(value.phoneNumber as string),
    );
    formData.systemPermissions = formData.systemPermissionSelectList?.map((value) => ({
      id: value[0],
    }));
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
          element: InputElement(
            selectListState,
            setSelectListState,
            systemPermissionState,
            setSystemPermissionState,
          ),
          props: {
            title: '添加单位',
          },
          formRef: createFormRef,
          onFinish: async (formData) => await createCompany(submitPreHandler(formData)),
        }}
        modifyEditModalForm={{
          element: InputElement(
            selectListState,
            setSelectListState,
            systemPermissionState,
            setSystemPermissionState,
          ),
          props: {
            title: '编辑单位',
          },
          formRef: modifyFormRef,
          onFinish: async (formData) =>
            await modifyCompany(formData.id, submitPreHandler(formData)),
          onConfirmRemove: async (id) => await removeCompany(id),
          queryData: async (id) => {
            const result = await queryCompany(id);
            result.data.systemPermissionSelectList = result.data.systemPermissions?.map(
              (systemPermission) => ({
                value: systemPermission.id,
                label: systemPermission.name,
              }),
            );
            return result;
          },
        }}
        onLoad={async () => {
          let list: API.SelectList[] = [
            {
              label: '无',
              title: '无',
              value: 0,
            },
          ];
          const parentIds = getCompanyParentIds();
          const companySelect = (await queryCompanySelectList(parentIds)).data;
          if (companySelect.length > 0) {
            list = [...list, ...companySelect];
          }
          setSelectListState(list);
          const systemPermissionBases = (await querySystemPermissionSelectList([0])).data;
          setSystemPermissionState(systemPermissionBases);
        }}
      />{' '}
    </MainPageContainer>
  );
};

export default Company;
