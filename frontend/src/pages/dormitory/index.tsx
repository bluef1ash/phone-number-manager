import React, { useEffect, useRef, useState } from 'react';
import DataList from '@/components/DataList';
import MainPageContainer from '@/components/MainPageContainer';
import type { ActionType } from '@ant-design/pro-table';
import type { ProFormInstance } from '@ant-design/pro-form';
import {
  ProFormDateRangePicker,
  ProFormDigit,
  ProFormSelect,
  ProFormText,
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
import {
  downloadExcelFile,
  getCompanyParentIds,
  submitPrePhoneNumberHandle,
} from '@/services/utils';
import { message, Spin, Upload } from 'antd';
import { dormitoryManagerImportExcel } from '@/services/api';
import { SESSION_TOKEN_KEY } from '@config/constant';
import PhoneNumberList from '@/components/PhoneNumberList';
import SelectCascder from '@/components/SelectCascder';
import { querySubcontractorSelectList } from '@/services/subcontractor/api';

const InputElement = (
  subcontractorSelectState: API.SelectList[],
  setSubcontractorSelectState: React.Dispatch<React.SetStateAction<API.SelectList[]>>,
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
            /^([1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[\dXx])|([1-9]\d{5}\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3})$/g,
          message: '社区居民楼片长身份证号码不正确！',
        },
      ]}
    />{' '}
    <ProFormSelect
      name="politicalStatus"
      width="xl"
      label="社区居民楼片长政治状况"
      valueEnum={{
        0: '群众',
        1: '共产党员',
        2: '预备共产党员',
        3: '共青团员',
        4: '预备共青团员',
        5: '民主党派',
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
      valueEnum={{
        0: '在职',
        1: '退休',
        2: '无业',
        3: '失业',
        4: '自由职业',
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
      valueEnum={{
        0: '文盲',
        1: '小学',
        2: '初级中学',
        3: '中学专科',
        4: '高级中学',
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
    <SelectCascder
      width="xl"
      name="subcontractorInfo"
      label="社区居民楼片长所属分包人"
      requiredMessage="社区居民楼片长所属分包人不能为空！"
      selectState={subcontractorSelectState}
      setSelectState={setSubcontractorSelectState}
      querySelectList={async (value) => (await querySubcontractorSelectList([value])).data}
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
  const formRef = useRef<ProFormInstance<API.DormitoryManager>>();
  const [spinState, setSpinState] = useState<boolean>(false);
  const [spinTipState, setSpinTipState] = useState<string>('');
  const [subcontractorSelectState, setSubcontractorSelectState] = useState<API.SelectList[]>([]);

  useEffect(() => {
    (async () => {
      const parentIds = getCompanyParentIds();
      setSubcontractorSelectState((await querySubcontractorSelectList(parentIds)).data);
    })();
  }, []);

  const submitPreHandler = (formData: API.DormitoryManager) => {
    formData.phoneNumbers = formData.phoneNumbers?.map((value: API.PhoneNumber) =>
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
              renderFormItem(item, { onChange }) {
                return <ProFormDateRangePicker fieldProps={{ onChange }} />;
              },
            },
            {
              title: '社区居民楼片长地址',
              dataIndex: 'address',
              sorter: true,
              ellipsis: true,
            },
            {
              title: (schema, type) => (type === 'table' ? '所属分包人' : '所属单位或分包人'),
              dataIndex: ['subcontractor', 'name'],
              sorter: true,
              ellipsis: true,
              renderFormItem(item, { onChange }) {
                return (
                  <SelectCascder
                    selectState={subcontractorSelectState}
                    setSelectState={setSubcontractorSelectState}
                    querySelectList={async (value) =>
                      (await querySubcontractorSelectList([value])).data
                    }
                    cascaderFieldProps={{
                      multiple: true,
                      onChange,
                    }}
                  />
                );
              },
            },
            {
              title: '所属社区',
              dataIndex: ['company', 'name'],
              sorter: true,
              ellipsis: true,
              search: false,
            },
            {
              title: '联系方式',
              dataIndex: 'phoneNumbers',
              ellipsis: true,
              render(dom, entity) {
                return (
                  <>
                    {entity.phoneNumbers?.map(
                      (phoneNumber: API.PhoneNumber, index: number) =>
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
          modalForm={{
            title: '社区居民楼片长',
            element: InputElement(subcontractorSelectState, setSubcontractorSelectState),
            formRef: formRef,
            onCreateFinish: async (formData) =>
              await createDormitoryManager(submitPreHandler(formData)),
            onModifyFinish: async (formData) =>
              await modifyDormitoryManager(formData.id, submitPreHandler(formData)),
            queryData: async (id) => {
              const result = await queryDormitoryManager(id);
              result.data.education = result.data.education.toString();
              result.data.politicalStatus = result.data.politicalStatus.toString();
              result.data.employmentStatus = result.data.employmentStatus.toString();
              return result;
            },
          }}
          removeData={async (id) => await removeDormitoryManager(id)}
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
        />{' '}
      </MainPageContainer>{' '}
    </Spin>
  );
};

export default DormitoryManager;
