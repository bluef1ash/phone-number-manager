import React, { useRef, useState } from 'react';
import DataList from '@/components/DataList';
import MainPageContainer from '@/components/MainPageContainer';
import type { ActionType } from '@ant-design/pro-table';
import type { ProFormInstance } from '@ant-design/pro-form';
import {
  ProFormDateRangePicker,
  ProFormDigit,
  ProFormSelect,
  ProFormText,
  ProFormTreeSelect,
} from '@ant-design/pro-form';
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
import {
  downloadExcelFile,
  getCompanyParentIds,
  submitPrePhoneNumberHandle,
} from '@/services/utils';
import { message, Spin, Upload } from 'antd';
import { dormitoryManagerImportExcel } from '@/services/api';
import { SESSION_TOKEN_KEY } from '@config/constant';
import { querySystemUserSelectList } from '@/services/user/api';
import PhoneNumberList from '@/components/PhoneNumberList';
import SelectCascder from '@/components/SelectCascder';

const InputElement = (
  systemUsersSelectState: API.SelectList[],
  setSystemUsersSelectState: React.Dispatch<React.SetStateAction<API.SelectList[]>>,
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
    <ProFormText
      name="idNumber"
      width="xl"
      label="社区居民楼片长身份证号码"
      rules={[
        {
          required: true,
          message: '社区居民楼片长身份证号码不能为空！',
        },
        {
          pattern:
            /^([1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx])|([1-9]\d{5}\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3})$/g,
          message: '社区居民楼片长身份证号码不正确！',
        },
      ]}
    />{' '}
    <ProFormSelect
      name="politicalStatus"
      width="xl"
      label="社区居民楼片长政治状况"
      placeholder="请选择"
      options={[
        { value: 0, label: '群众' },
        { value: 1, label: '共产党员' },
        { value: 2, label: '预备共产党员' },
        { value: 3, label: '共青团员' },
        { value: 4, label: '预备共青团员' },
        { value: 5, label: '民主党派' },
      ]}
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
      options={[
        { value: 0, label: '在职' },
        { value: 1, label: '退休' },
        { value: 2, label: '无业' },
        { value: 3, label: '失业' },
        { value: 4, label: '自由职业' },
      ]}
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
      options={[
        { value: 0, label: '文盲' },
        { value: 1, label: '小学' },
        { value: 2, label: '初级中学' },
        { value: 3, label: '中学专科' },
        { value: 4, label: '高级中学' },
        { value: 5, label: '大学专科' },
        { value: 6, label: '大学本科' },
        { value: 7, label: '硕士研究生' },
        { value: 8, label: '博士研究生' },
      ]}
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
    <SelectCascder
      name="subcontractorInfo"
      label="社区居民楼片长所属分包人"
      requiredMessage="社区居民楼片长所属分包人不能为空！"
      selectState={systemUsersSelectState}
      setSelectState={setSystemUsersSelectState}
      querySelectList={async (value) => (await querySystemUserSelectList([value])).data}
    />{' '}
    <PhoneNumberList
      name="phoneNumbers"
      initialValue={[{ phoneNumber: '' }]}
      creatorButtonText="添加社区居民楼片长的联系方式"
      listValidRejectMessage="请添加社区居民楼片长的联系方式！"
      phoneNumberFormProps={{
        name: 'phoneNumber',
        label: '社区居民楼片长的联系方式',
        placeholder: '请输入社区居民楼片长的联系方式',
      }}
    />{' '}
  </>
);

const DormitoryManager: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const createFormRef = useRef<ProFormInstance<API.DormitoryManager>>();
  const modifyFormRef = useRef<ProFormInstance<API.DormitoryManager>>();
  const [companiesState, setCompaniesState] = useState<API.SelectList[]>([]);
  const [spinState, setSpinState] = useState<boolean>(false);
  const [spinTipState, setSpinTipState] = useState<string>('');
  const [systemUsersSelectState, setSystemUsersSelectState] = useState<API.SelectList[]>([]);

  const submitPreHandler = (formData: API.DormitoryManager) => {
    formData.phoneNumbers = formData.phoneNumbers?.map((value) =>
      submitPrePhoneNumberHandle(value.phoneNumber as string),
    );
    formData.subcontractorInfo = formData.subcontractorInfo?.slice(-2);
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
              title: '社区居民楼片长身份证号码',
              dataIndex: 'idNumber',
              sorter: true,
              ellipsis: true,
              hideInTable: true,
            },
            {
              title: '社区居民楼片长性别',
              dataIndex: 'gender',
              sorter: true,
              ellipsis: true,
              valueEnum: {
                0: {
                  text: '女',
                },
                1: {
                  text: '男',
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
                return (
                  <SelectCascder
                    selectState={systemUsersSelectState}
                    setSelectState={setSystemUsersSelectState}
                    querySelectList={async (value) =>
                      (await querySystemUserSelectList([value])).data
                    }
                  />
                );
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
            element: InputElement(systemUsersSelectState, setSystemUsersSelectState),
            props: {
              title: '添加社区居民楼片长',
            },
            formRef: createFormRef,
            onFinish: async (formData) => await createDormitoryManager(submitPreHandler(formData)),
          }}
          modifyEditModalForm={{
            element: InputElement(systemUsersSelectState, setSystemUsersSelectState),
            props: {
              title: '编辑社区居民楼片长',
            },
            formRef: modifyFormRef,
            onFinish: async (formData) =>
              await modifyDormitoryManager(formData.id as number, submitPreHandler(formData)),
            onConfirmRemove: async (id) => await removeDormitoryManager(id),
            queryData: async (id) => await queryDormitoryManager(id),
          }}
          importDataUploadProps={{
            action: dormitoryManagerImportExcel,
            name: 'file',
            headers: {
              Authorization: `Bearer_${localStorage.getItem(SESSION_TOKEN_KEY)}`,
            },
            async beforeUpload(file) {
              const isXlsx =
                file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
              if (!isXlsx) {
                message.error('只能上传Excel文件！');
              }
              return isXlsx || Upload.LIST_IGNORE;
            },
            async onChange({ file }) {
              if (file.status === 'done') {
                message.success('上传成功！');
                actionRef.current?.reload();
              } else if (file.status === 'error') {
                message.error('上传失败！');
              }
            },
          }}
          exportDataEventHandler={async () =>
            downloadExcelFile(
              setSpinState,
              setSpinTipState,
              downloadDormitoryManagerExcel(),
              ['正在生成社区居民楼片长花名册中...', '正在下载社区居民楼片长花名册中...'],
              '社区楼片长花名册.xlsx',
            )
          }
          onLoad={async () => {
            const parentIds = getCompanyParentIds();
            setCompaniesState((await queryCompanySelectList(parentIds)).data);
            setSystemUsersSelectState((await querySystemUserSelectList(parentIds)).data);
          }}
        />{' '}
      </MainPageContainer>{' '}
    </Spin>
  );
};

export default DormitoryManager;
