import React, { useEffect, useRef, useState } from 'react';
import DataList from '@/components/DataList';
import MainPageContainer from '@/components/MainPageContainer';
import type { ActionType } from '@ant-design/pro-table';
import type { ProFormInstance } from '@ant-design/pro-form';
import { ProFormText } from '@ant-design/pro-form';
import {
  batchCommunityResident,
  createCommunityResident,
  downloadCommunityResidentExcel,
  modifyCommunityResident,
  queryCommunityResident,
  queryCommunityResidentList,
  removeCommunityResident,
} from '@/services/resident/api';
import { downloadExcelFile, submitPrePhoneNumberHandle } from '@/services/utils';
import { Alert, message, Spin, Upload } from 'antd';
import { communityResidentImportExcel } from '@/services/api';
import { SESSION_TOKEN_KEY } from '@config/constant';
import PhoneNumberList from '@/components/PhoneNumberList';
import { ExceptionCode } from '@/services/enums';
import SelectCascder from '@/components/SelectCascder';
import { querySubcontractorSelectList } from '@/services/subcontractor/api';

const InputElement = (
  subcontractorSelectState: API.SelectList[],
  setSubcontractorSelectState: React.Dispatch<React.SetStateAction<API.SelectList[]>>,
  repeatExceptionMessageState: string | null,
) => (
  <>
    {repeatExceptionMessageState === null ? (
      <></>
    ) : (
      <Alert
        message={repeatExceptionMessageState}
        type="error"
        showIcon
        closable={true}
        style={{ marginBottom: 20 }}
      />
    )}{' '}
    <ProFormText
      width="xl"
      name="name"
      label="社区居民姓名"
      tooltip="不能超过10个字符"
      placeholder="请输入社区居民姓名"
      rules={[
        {
          required: true,
          message: '社区居民姓名不能为空！',
        },
        {
          max: 10,
          message: '社区居民姓名不能超过10个字符！',
        },
      ]}
    />{' '}
    <ProFormText
      width="xl"
      name="address"
      label="社区居民地址"
      tooltip="不能超过255个字符"
      placeholder="请输入社区居民地址"
      rules={[
        {
          required: true,
          message: '社区居民地址不能为空！',
        },
        {
          max: 255,
          message: '社区居民地址不能超过255个字符！',
        },
      ]}
    />{' '}
    <PhoneNumberList
      name="phoneNumbers"
      initialValue={[{ phoneNumber: '' }]}
      creatorButtonText="添加社区居民联系方式"
      listValidRejectMessage="请添加社区居民联系方式！"
      phoneNumberFormProps={{
        name: 'phoneNumber',
        label: '社区居民联系方式',
        placeholder: '请输入社区居民联系方式',
      }}
    />
    <SelectCascder
      name="subcontractorInfo"
      label="社区居民所属分包人"
      requiredMessage="社区居民所属分包人不能为空！"
      selectState={subcontractorSelectState}
      setSelectState={setSubcontractorSelectState}
      querySelectList={async (value) => (await querySubcontractorSelectList([value])).data}
    />{' '}
  </>
);

const CommunityResident: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const formRef = useRef<ProFormInstance<API.CommunityResident>>();
  const [spinState, setSpinState] = useState<boolean>(false);
  const [spinTipState, setSpinTipState] = useState<string>('');
  const [subcontractorSelectState, setSubcontractorSelectState] = useState<API.SelectList[]>([]);
  const [repeatExceptionMessageState, setRepeatExceptionMessageState] = useState<string | null>(
    null,
  );
  const [modifyCompanyIdState, setModifyCompanyIdState] = useState<string | null>(null);
  const [modifySubcontractorIdState, setModifySubcontractorIdState] = useState<string | null>(null);

  useEffect(() => {
    (async () => {
      setSubcontractorSelectState((await querySubcontractorSelectList([0])).data);
    })();
  }, []);

  const submitPreHandler = (formData: API.CommunityResident) => {
    formData.phoneNumbers = formData.phoneNumbers?.map((value: API.PhoneNumber) =>
      submitPrePhoneNumberHandle(value.phoneNumber as string),
    );
    if (modifyCompanyIdState === null) {
      formData.companyId = formData.subcontractorInfo[formData.subcontractorInfo.length - 2];
    } else {
      formData.companyId = modifyCompanyIdState;
      setModifyCompanyIdState(null);
    }
    if (modifySubcontractorIdState === null) {
      formData.subcontractorId = formData.subcontractorInfo[formData.subcontractorInfo.length - 1];
    } else {
      formData.subcontractorId = modifySubcontractorIdState;
      setModifySubcontractorIdState(null);
    }
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
            breadcrumbName: '社区居民管理',
          },
          {
            path: '/resident',
            breadcrumbName: '社区居民列表',
          },
        ]}
      >
        {' '}
        <DataList<API.CommunityResident, API.CommunityResident>
          customActionRef={actionRef}
          customRequest={async (params) => await queryCommunityResidentList(params)}
          customColumns={[
            {
              title: '社区居民姓名',
              dataIndex: 'name',
              sorter: true,
              ellipsis: true,
            },
            {
              title: '社区居民地址',
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
            await batchCommunityResident({
              method: 'DELETE',
              data,
            })
          }
          modalForm={{
            title: '社区居民',
            element: InputElement(
              subcontractorSelectState,
              setSubcontractorSelectState,
              repeatExceptionMessageState,
            ),
            onCancel: () => {
              setRepeatExceptionMessageState(null);
            },
            formRef: formRef,
            onCreateFinish: async (formData) => {
              const result = await createCommunityResident(submitPreHandler(formData));
              if (result.code === ExceptionCode.REPEAT_DATA_FAILED) {
                setRepeatExceptionMessageState(result.message);
              }
              return result;
            },
            onModifyFinish: async (formData) => {
              const result = await modifyCommunityResident(formData.id, submitPreHandler(formData));
              if (result.code === ExceptionCode.REPEAT_DATA_FAILED) {
                setRepeatExceptionMessageState(result.message);
              } else {
                setRepeatExceptionMessageState(null);
              }
              return result;
            },
            queryData: async (id) => {
              const result = await queryCommunityResident(id);
              setModifyCompanyIdState(result.data.companyId);
              setModifySubcontractorIdState(result.data.subcontractorId);
              return result;
            },
          }}
          removeData={async (id) => await removeCommunityResident(id)}
          importDataUploadProps={{
            action: communityResidentImportExcel,
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
              downloadCommunityResidentExcel(),
              ['正在生成社区居民花名册中...', '正在下载社区居民花名册中...'],
              '“评社区”活动电话库登记表.xlsx',
            )
          }
        />{' '}
      </MainPageContainer>{' '}
    </Spin>
  );
};

export default CommunityResident;
