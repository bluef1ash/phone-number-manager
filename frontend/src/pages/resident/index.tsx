import React, { useRef, useState } from 'react';
import DataList from '@/components/DataList';
import MainPageContainer from '@/components/MainPageContainer';
import type { ActionType } from '@ant-design/pro-table';
import type { ProFormInstance } from '@ant-design/pro-form';
import { ProFormText, ProFormTreeSelect } from '@ant-design/pro-form';
import {
  batchCommunityResident,
  createCommunityResident,
  modifyCommunityResident,
  queryCommunityResident,
  queryCommunityResidentList,
  removeCommunityResident,
} from '@/services/resident/api';
import { queryCompanySelectList } from '@/services/company/api';
import { downloadExcelFile, submitPrePhoneNumberHandle } from '@/services/utils';
import { Alert, message, Spin, Upload } from 'antd';
import { communityResidentImportExcel } from '@/services/api';
import { SESSION_TOKEN_KEY } from '@config/constant';
import { querySystemUserSelectList } from '@/services/user/api';
import PhoneNumberList from '@/components/PhoneNumberList';
import Subcontractor from '@/components/Subcontractor';
import { ExceptionCode } from '@/services/enums';

const InputElement = (
  systemUsersSelectState: API.SelectList[],
  setSystemUsersSelectState: React.Dispatch<React.SetStateAction<API.SelectList[]>>,
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
    <Subcontractor
      name="subcontractorInfo"
      label="社区居民所属分包人"
      requiredMessage="社区居民所属分包人不能为空！"
      systemUsersSelectState={systemUsersSelectState}
      setSystemUsersSelectState={setSystemUsersSelectState}
    />{' '}
  </>
);

const CommunityResident: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const createFormRef = useRef<ProFormInstance<API.CommunityResident>>();
  const modifyFormRef = useRef<ProFormInstance<API.CommunityResident>>();
  const [companiesState, setCompaniesState] = useState<API.SelectList[]>([]);
  const [spinState, setSpinState] = useState<boolean>(false);
  const [spinTipState, setSpinTipState] = useState<string>('');
  const [systemUsersSelectState, setSystemUsersSelectState] = useState<API.SelectList[]>([]);
  const [repeatExceptionMessageState, setRepeatExceptionMessageState] = useState<string | null>(
    null,
  );

  const submitPreHandler = (formData: API.CommunityResident) => {
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
              title: '所属分包人',
              dataIndex: ['systemUser', 'username'],
              sorter: true,
              ellipsis: true,
              renderFormItem() {
                return (
                  <ProFormTreeSelect request={async () => (await queryCompanySelectList()).data} />
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
            await batchCommunityResident({
              method: 'DELETE',
              data,
            })
          }
          createEditModalForm={{
            element: InputElement(
              systemUsersSelectState,
              setSystemUsersSelectState,
              repeatExceptionMessageState,
            ),
            props: {
              title: '添加社区居民',
            },
            formRef: createFormRef,
            onFinish: async (formData) => {
              const result = await createCommunityResident(submitPreHandler(formData));
              if (result.code === ExceptionCode.REPEAT_DATA_FAILED) {
                setRepeatExceptionMessageState(result.message);
              }
              return result;
            },
          }}
          modifyEditModalForm={{
            element: InputElement(
              systemUsersSelectState,
              setSystemUsersSelectState,
              repeatExceptionMessageState,
            ),
            props: {
              title: '编辑社区居民',
            },
            formRef: modifyFormRef,
            onFinish: async (formData) => {
              const result = await modifyCommunityResident(
                formData.id as number,
                submitPreHandler(formData),
              );
              if (result.code === ExceptionCode.REPEAT_DATA_FAILED) {
                setRepeatExceptionMessageState(result.message);
              }
              return result;
            },
            onConfirmRemove: async (id) => await removeCommunityResident(id),
            queryData: async (id) => await queryCommunityResident(id),
          }}
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
          exportDataEventHandler={async () => downloadExcelFile(setSpinState, setSpinTipState)}
          onLoad={async () => {
            setCompaniesState((await queryCompanySelectList()).data);
            setSystemUsersSelectState((await querySystemUserSelectList(null)).data);
          }}
        />{' '}
      </MainPageContainer>{' '}
    </Spin>
  );
};

export default CommunityResident;
