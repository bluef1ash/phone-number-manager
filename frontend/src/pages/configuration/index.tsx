import React, { useRef } from 'react';
import DataList from '@/components/DataList';
import MainPageContainer from '@/components/MainPageContainer';
import {
  batchConfiguration,
  createConfiguration,
  modifyConfiguration,
  queryConfiguration,
  queryConfigurationList,
  removeConfiguration,
} from '@/services/configuration/api';
import type { ActionType } from '@ant-design/pro-table';
import type { ProFormInstance } from '@ant-design/pro-form';
import { ProFormSelect, ProFormText } from '@ant-design/pro-form';

const InputElement = (
  <>
    <ProFormText
      width="xl"
      name="title"
      label="系统配置项标题"
      tooltip="不能超过100个字符"
      placeholder="请输入系统配置项标题"
      rules={[
        {
          required: true,
          message: '系统配置标题不能为空！',
        },
        {
          max: 100,
          message: '系统配置标题不能超过100个字符！',
        },
      ]}
    />{' '}
    <ProFormText
      width="xl"
      name="description"
      label="系统配置项描述"
      tooltip="不能超过255个字符"
      placeholder="请输入系统配置项描述"
      rules={[
        {
          max: 255,
          message: '系统配置项描述不能超过255个字符！',
        },
      ]}
    />{' '}
    <ProFormText
      width="xl"
      name="name"
      label="系统配置变量名称"
      tooltip="不能超过100个字符"
      placeholder="请输入系统配置变量名称"
      rules={[
        {
          required: true,
          message: '系统配置变量名称不能为空！',
        },
        {
          max: 100,
          message: '系统配置变量名称不能超过100个字符！',
        },
      ]}
    />{' '}
    <ProFormText
      width="xl"
      name="content"
      label="系统配置变量值"
      placeholder="请输入系统配置变量值"
      rules={[
        {
          required: true,
          message: '系统配置变量值不能为空！',
        },
        {
          max: 255,
          message: '系统配置变量值不能超过255个字符！',
        },
      ]}
    />{' '}
    <ProFormSelect
      width="xl"
      valueEnum={{
        UNKNOWN: '未知类型',
        BOOLEAN: '布尔类型',
        STRING: '字符串类型',
        NUMBER: '数值类型',
        SYSTEM_USER: '系统用户类型',
      }}
      name="fieldType"
      label="系统配置字段类型"
      rules={[
        {
          required: true,
          message: '系统配置字段类型不能为空！',
        },
      ]}
    />{' '}
    <ProFormText
      width="xl"
      name="fieldValue"
      label="系统配置字段值"
      tooltip="不能超过255个字符"
      placeholder="请输入系统配置字段值"
      rules={[
        {
          max: 255,
          message: '系统配置字段值不能超过255个字符！',
        },
      ]}
    />{' '}
  </>
);

const Configuration: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const createFormRef = useRef<ProFormInstance<API.Configuration>>();
  const ModifyFormRef = useRef<ProFormInstance<API.Configuration>>();
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
          path: '/system/configuration',
          breadcrumbName: '系统配置列表',
        },
      ]}
    >
      {' '}
      <DataList<API.Configuration, API.Configuration>
        customActionRef={actionRef}
        customRequest={async (params) => await queryConfigurationList(params)}
        customColumns={[
          {
            title: '标题',
            dataIndex: 'title',
            ellipsis: true,
            tip: '标题过长会自动收缩',
            sorter: true,
          },
          {
            title: '描述',
            dataIndex: 'description',
            tip: '描述过长会自动收缩',
            sorter: true,
            ellipsis: true,
          },
        ]}
        batchRemoveEventHandler={async (data) =>
          (
            await batchConfiguration<number[]>({
              method: 'DELETE',
              data,
            })
          ).code === 200
        }
        createEditModalForm={{
          element: InputElement,
          props: {
            title: '添加系统配置项',
          },
          formRef: createFormRef,
          onFinish: async (formData) => (await createConfiguration(formData)).code === 200,
        }}
        modifyEditModalForm={{
          element: InputElement,
          props: {
            title: '编辑系统配置项',
          },
          formRef: ModifyFormRef,
          onFinish: async (formData) => {
            const { code } = await modifyConfiguration(formData.id, formData);
            return code === 0;
          },
          onConfirmRemove: async (id) => (await removeConfiguration(id)).code === 200,
          queryData: async (id) => await queryConfiguration(id),
        }}
      />{' '}
    </MainPageContainer>
  );
};

export default Configuration;
