import React, { useEffect, useRef, useState } from 'react';
import DataList from '@/components/DataList';
import MainPageContainer from '@/components/MainPageContainer';
import type { ActionType } from '@ant-design/pro-table';
import type { ProFormInstance } from '@ant-design/pro-form';
import {
  ProFormDatePicker,
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
  modifyDormitoryManager,
  queryDormitoryManager,
  queryDormitoryManagerList,
  removeDormitoryManager,
} from '@/services/dormitory/api';
import { queryCompanySelectList } from '@/services/company/api';
import { querySystemUserSelectList } from '@/services/user/api';

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
          value: 'MALE',
        },
        {
          label: '女',
          value: 'FEMALE',
        },
        {
          label: '未知',
          value: 'UNKNOWN',
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
        MALE: '群众',
        PARTY_MEMBER: '共产党员',
        PREPARATORY_COMMUNISTS: '预备共产党员',
        COMMUNIST_YOUTH_LEAGUE_MEMBER: '共青团员',
        PREPARING_COMMUNIST_YOUTH_LEAGUE_MEMBER: '预备共青团员',
        OTHER: '其他',
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
        WORK: '在职',
        RETIREMENT: '退休',
        UNEMPLOYED: '无业',
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
        ILLITERACY: '文盲',
        PRIMARY_SCHOOL: '小学',
        JUNIOR_HIGH_SCHOOL: '初中',
        TECHNICAL_SECONDARY_SCHOOL: '中学专科',
        SENIOR_MIDDLE_SCHOOL: '高中',
        JUNIOR_COLLEGE: '大学专科',
        UNDERGRADUATE_COURSE: '大学本科',
        MASTER: '硕士研究生',
        DOCTOR: '博士研究生',
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

  useEffect(() => {
    const getCompanies = async () => (await queryCompanySelectList()).data;
    const getSystemUsers = async () => (await querySystemUserSelectList()).data;
    getCompanies().then((value) => setCompaniesState(value));
    getSystemUsers().then((value) => setSystemUsersState(value));
  }, []);

  const submitPreHandler = (formData: API.DormitoryManager) => {
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
              MALE: {
                text: '男',
              },
              FEMALE: {
                text: '女',
              },
              UNKNOWN: {
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
              return <ProFormDigit name="age" max={110} />;
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
              return <ProFormSelect name="systemUserId" request={async () => systemUsersState} />;
            },
          },
          {
            title: '所属社区',
            dataIndex: ['company', 'name'],
            sorter: true,
            ellipsis: true,
            renderFormItem() {
              return <ProFormTreeSelect name="companyId" request={async () => companiesState} />;
            },
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
      />{' '}
    </MainPageContainer>
  );
};

export default DormitoryManager;
