import React, { useEffect, useRef, useState } from 'react';
import DataList from '@/components/DataList';
import MainPageContainer from '@/components/MainPageContainer';
import type { ActionType } from '@ant-design/pro-table';
import type { ProFormInstance } from '@ant-design/pro-form';
import {
  ProFormDatePicker,
  ProFormDateRangePicker,
  ProFormDigit,
  ProFormList,
  ProFormRadio,
  ProFormSelect,
  ProFormText,
  ProFormTreeSelect,
} from '@ant-design/pro-form';
import type { RuleObject, StoreValue } from 'rc-field-form/lib/interface';
import { isArray } from 'lodash';
import { parsePhoneNumber } from 'libphonenumber-js/max';
import {
  batchDormitoryManager,
  createDormitoryManager,
  downloadDormitoryManagerExcel,
  modifyDormitoryManager,
  queryDormitoryManager,
  queryDormitoryManagerList,
  removeDormitoryManager,
} from '@/services/dormitory/api';
import { queryCompanySelectList } from '@/services/company/api';
import { querySystemUserSelectList } from '@/services/user/api';
import { submitPrePhoneNumberHandle } from '@/services/utils';
import { Dropdown, message, Spin } from 'antd';
import { DownOutlined, ExportOutlined } from '@ant-design/icons';

const InputElement = (
  companiesState: API.SelectList[],
  systemUsersState: API.SelectList[],
  companyLowState: API.SelectList[] | null | undefined,
  setCompanyLowState: { (value: API.SelectList[] | null | undefined): void },
) => (
  <>
    <ProFormText
      width="xl"
      name="name"
      label="社区居民楼片长姓名"
      tooltip="不能超过10个字符"
      placeholder="请输入社区居民楼片长姓名"
      rules={[
        {
          required: true,
          message: '社区居民楼片长姓名不能为空！',
        },
        {
          max: 10,
          message: '社区居民楼片长姓名不能超过10个字符！',
        },
      ]}
    />{' '}
    <ProFormRadio.Group
      name="gender"
      label="社区居民楼片长性别"
      options={[
        {
          label: '男',
          value: 0,
        },
        {
          label: '女',
          value: 1,
        },
        {
          label: '未知',
          value: 2,
        },
      ]}
      rules={[
        {
          required: true,
          message: '社区居民楼片长性别不能为空！',
        },
      ]}
    />{' '}
    <ProFormDatePicker
      name="birth"
      width="xl"
      label="社区居民楼片长出生年月"
      rules={[
        {
          required: true,
          message: '社区居民楼片长出生年月不能为空！',
        },
      ]}
    />{' '}
    <ProFormSelect
      name="politicalStatus"
      width="xl"
      label="社区居民楼片长政治状况"
      placeholder="请选择"
      valueEnum={{
        0: '群众',
        1: '共产党员',
        2: '预备共产党员',
        3: '共青团员',
        4: '预备共青团员',
        5: '其他',
      }}
      rules={[
        {
          required: true,
          message: '社区居民楼片长政治状况不能为空！',
        },
      ]}
    />{' '}
    <ProFormSelect
      name="employmentStatus"
      width="xl"
      label="社区居民楼片长就业情况"
      placeholder="请选择"
      valueEnum={{
        0: '在职',
        1: '退休',
        2: '无业',
      }}
      rules={[
        {
          required: true,
          message: '社区居民楼片长就业情况不能为空！',
        },
      ]}
    />{' '}
    <ProFormSelect
      name="education"
      width="xl"
      label="社区居民楼片长教育情况"
      placeholder="请选择"
      valueEnum={{
        0: '文盲',
        1: '小学',
        2: '初中',
        3: '中学专科',
        4: '高中',
        5: '大学专科',
        6: '大学本科',
        7: '硕士研究生',
        8: '博士研究生',
      }}
      rules={[
        {
          required: true,
          message: '社区居民楼片长教育情况不能为空！',
        },
      ]}
    />{' '}
    <ProFormText
      width="xl"
      name="address"
      label="社区居民楼片长地址"
      tooltip="不能超过255个字符"
      placeholder="请输入社区居民楼片长地址"
      rules={[
        {
          required: true,
          message: '社区居民楼片长地址不能为空！',
        },
        {
          max: 255,
          message: '社区居民楼片长地址不能超过255个字符！',
        },
      ]}
    />{' '}
    <ProFormText
      width="xl"
      name="managerAddress"
      label="社区居民楼片长管理地址"
      tooltip="不能超过255个字符"
      placeholder="请输入社区居民楼片长管理地址"
      rules={[
        {
          required: true,
          message: '社区居民楼片长管理地址不能为空！',
        },
        {
          max: 255,
          message: '社区居民楼片长管理地址不能超过255个字符！',
        },
      ]}
    />{' '}
    <ProFormDigit
      name="managerCount"
      label="社区居民楼片长管理数"
      min={1}
      fieldProps={{ precision: 0 }}
      rules={[
        {
          required: true,
          message: '社区居民楼片长管理数不能为空！',
        },
      ]}
    />{' '}
    <ProFormSelect
      name="systemUserId"
      width="xl"
      label="社区居民楼片长所属分包人"
      placeholder="请选择"
      request={async () => systemUsersState}
      rules={[
        {
          required: true,
          message: '社区居民楼片长所属分包人不能为空！',
        },
      ]}
    />{' '}
    <ProFormTreeSelect
      name="companyId"
      width="xl"
      label="社区居民楼片长所属社区"
      placeholder="请选择"
      request={async () => companiesState}
      fieldProps={{
        // @ts-ignore
        onSelect(value: number, node: API.SelectList) {
          setCompanyLowState(node.children);
        },
      }}
      rules={[
        {
          required: true,
          message: '社区居民楼片长所属社区不能为空！',
        },
        {
          validator() {
            return companyLowState !== null
              ? Promise.reject('只能选择社区一级！')
              : Promise.resolve();
          },
        },
      ]}
    />{' '}
    <ProFormList
      name="phoneNumbers"
      creatorButtonProps={{
        creatorButtonText: '添加社区居民楼片长联系方式',
      }}
      rules={[
        {
          validator(rule: RuleObject, value: StoreValue) {
            return typeof value !== 'undefined' && isArray(value)
              ? Promise.resolve()
              : Promise.reject('请添加社区居民楼片长联系方式！');
          },
        },
      ]}
    >
      {' '}
      <ProFormText
        name="phoneNumber"
        width="xl"
        label="社区居民楼片长联系方式"
        placeholder="请输入社区居民楼片长联系方式"
        rules={[
          {
            required: true,
            message: '社区居民楼片长联系方式不能为空！',
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

const DormitoryManager: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const createFormRef = useRef<ProFormInstance<API.DormitoryManager>>();
  const modifyFormRef = useRef<ProFormInstance<API.DormitoryManager>>();
  const [companiesState, setCompaniesState] = useState<API.SelectList[]>([]);
  const [companyLowState, setCompanyLowState] = useState<API.SelectList[] | null | undefined>(null);
  const [systemUsersState, setSystemUsersState] = useState<API.SelectList[]>([]);
  const [spinState, setSpinState] = useState<boolean>(false);
  const [spinTipState, setSpinTipState] = useState<string>('');

  useEffect(() => {
    const getCompanies = async () => (await queryCompanySelectList()).data;
    const getSystemUsers = async () => (await querySystemUserSelectList()).data;
    getCompanies().then((value) => setCompaniesState(value));
    getSystemUsers().then((value) => setSystemUsersState(value));
  }, []);

  const submitPreHandler = (formData: API.DormitoryManager) => {
    formData.phoneNumbers = formData.phoneNumbers?.map((value) =>
      submitPrePhoneNumberHandle(value.phoneNumber as string),
    );
    return formData;
  };

  return (
    <Spin spinning={spinState} tip={spinTipState}>
      {' '}
      <MainPageContainer
        routes={[
          {
            path: '/welcome',
            breadcrumbName: '首页',
          },
          {
            path: '',
            breadcrumbName: '社区居民楼片长管理',
          },
          {
            path: '/dormitory',
            breadcrumbName: '社区居民楼片长列表',
          },
        ]}
      >
        {' '}
        <DataList<API.DormitoryManager, API.DormitoryManager>
          customActionRef={actionRef}
          customRequest={async (params) => await queryDormitoryManagerList(params)}
          customColumns={[
            {
              title: '社区居民楼片长姓名',
              dataIndex: 'name',
              sorter: true,
              ellipsis: true,
            },
            {
              title: '社区居民楼片长性别',
              dataIndex: 'gender',
              sorter: true,
              ellipsis: true,
              valueEnum: {
                0: {
                  text: '男',
                },
                1: {
                  text: '女',
                },
                2: {
                  text: '未知',
                },
              },
            },
            {
              title: '社区居民楼片长年龄',
              dataIndex: 'age',
              sorter: true,
              ellipsis: true,
              renderFormItem() {
                return <ProFormDateRangePicker />;
              },
            },
            {
              title: '社区居民楼片长地址',
              dataIndex: 'address',
              sorter: true,
              ellipsis: true,
            },
            {
              title: '所属分包人',
              dataIndex: ['systemUser', 'username'],
              sorter: true,
              ellipsis: true,
              renderFormItem() {
                return <ProFormSelect mode="multiple" request={async () => systemUsersState} />;
              },
            },
            {
              title: '所属社区',
              dataIndex: ['company', 'name'],
              sorter: true,
              ellipsis: true,
              renderFormItem() {
                return (
                  <ProFormTreeSelect
                    fieldProps={{ treeCheckable: true }}
                    request={async () => companiesState}
                  />
                );
              },
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
            await batchDormitoryManager({
              method: 'DELETE',
              data,
            })
          }
          createEditModalForm={{
            element: InputElement(
              companiesState,
              systemUsersState,
              companyLowState,
              setCompanyLowState,
            ),
            props: {
              title: '添加社区居民楼片长',
            },
            formRef: createFormRef,
            onFinish: async (formData) => await createDormitoryManager(submitPreHandler(formData)),
          }}
          modifyEditModalForm={{
            element: InputElement(
              companiesState,
              systemUsersState,
              companyLowState,
              setCompanyLowState,
            ),
            props: {
              title: '编辑社区居民楼片长',
            },
            formRef: modifyFormRef,
            onFinish: async (formData) => await modifyDormitoryManager(submitPreHandler(formData)),
            onConfirmRemove: async (id) => await removeDormitoryManager(id),
            queryData: async (id) => await queryDormitoryManager(id),
          }}
          importDataButton={<></>}
          exportDataButton={
            <Dropdown.Button
              icon={<DownOutlined />}
              onClick={async () => {
                setSpinTipState('正在生成社区楼片长花名册中...');
                setSpinState(true);
                const { data, response } = await downloadDormitoryManagerExcel();
                if (typeof data.code === 'undefined' && data) {
                  let filename = response.headers.get('Content-Disposition');
                  if (filename !== null) {
                    filename = decodeURI(filename.substring('attachment;filename='.length));
                  } else {
                    filename = '街道（园区）社区楼片长花名册.xlsx';
                  }
                  const blob = await data;
                  setSpinTipState('正在下载社区楼片长花名册中...');
                  const link = document.createElement('a');
                  if ('download' in link) {
                    link.style.display = 'none';
                    link.href = URL.createObjectURL(blob);
                    link.download = filename;
                    document.body.appendChild(link);
                    link.click();
                    URL.revokeObjectURL(link.href);
                    document.body.removeChild(link);
                  } else {
                    //@ts-ignore
                    navigator.msSaveBlob(blob, filename);
                  }
                } else {
                  message.error('导出失败，请稍后再试！');
                }
                setSpinState(false);
              }}
              overlay={<ProFormTreeSelect request={async () => companiesState} />}
            >
              {' '}
              <ExportOutlined /> 导出数据{' '}
            </Dropdown.Button>
          }
        />{' '}
      </MainPageContainer>{' '}
    </Spin>
  );
};

export default DormitoryManager;
