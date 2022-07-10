import React, { useRef, useState } from 'react';
import DataList from '@/components/DataList';
import MainPageContainer from '@/components/MainPageContainer';
import type { ActionType } from '@ant-design/pro-table';
import type { ProFormInstance } from '@ant-design/pro-form';
import { ProFormList, ProFormText } from '@ant-design/pro-form';
import { queryCompanySelectList } from '@/services/company/api';
import { getCompanyParentIds, submitPrePhoneNumberHandle } from '@/services/utils';
import PhoneNumberList from '@/components/PhoneNumberList';
import SelectCascder from '@/components/SelectCascder';
import {
  batchSubcontractor,
  createSubcontractor,
  modifySubcontractor,
  querySubcontractor,
  querySubcontractorList,
  removeSubcontractor,
} from '@/services/subcontractor/api';

const InputElement = (
  selectListState: API.SelectList[],
  setSelectListState: React.Dispatch<React.SetStateAction<API.SelectList[]>>,
) => (
  <>
    <ProFormText
      width="xl"
      name="name"
      label="社区分包人员姓名"
      tooltip="不能超过20个字符"
      placeholder="请输入社区分包人员姓名"
      rules={[
        {
          required: true,
          message: '社区分包人员姓名不能为空！',
        },
        {
          max: 20,
          message: '社区分包人员姓名不能超过20个字符！',
        },
      ]}
    />{' '}
    <ProFormText
      width="xl"
      name="idCardNumber"
      label="身份证号码"
      placeholder="请输入社区分包人员身份证号码"
      rules={[
        {
          required: true,
          message: '身份证号码不能为空！',
        },
        {
          pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/,
          message: '身份证号码格式不正确！',
        },
      ]}
    />{' '}
    <ProFormList
      name="positions"
      creatorButtonProps={{
        creatorButtonText: '添加社区分包人员职务',
      }}
    >
      {' '}
      <ProFormText
        width="xl"
        name="position"
        label="社区分包人员职务"
        tooltip="不能超过10个字符"
        placeholder="请输入社区分包人员职务"
        rules={[
          {
            max: 10,
            message: '社区分包人员职务不能超过10个字符！',
          },
        ]}
      />{' '}
    </ProFormList>{' '}
    <ProFormList
      name="titles"
      creatorButtonProps={{
        creatorButtonText: '添加社区分包人员职称',
      }}
    >
      {' '}
      <ProFormText
        width="xl"
        name="title"
        label="社区分包人员职称"
        tooltip="不能超过10个字符"
        placeholder="请输入社区分包人员职称"
        rules={[
          {
            max: 10,
            message: '社区分包人员职称不能超过10个字符！',
          },
        ]}
      />{' '}
    </ProFormList>{' '}
    <PhoneNumberList
      name="phoneNumbers"
      initialValue={[{ phoneNumber: '' }]}
      creatorButtonText="添加联系方式"
      listValidRejectMessage="请添加联系方式！"
      phoneNumberFormProps={{
        name: 'phoneNumber',
        label: '联系方式',
        placeholder: '请输入联系方式',
      }}
    />{' '}
    <SelectCascder
      width="xl"
      name="companyId"
      label="所属单位"
      querySelectList={async (value) => (await queryCompanySelectList([value])).data}
      selectState={selectListState}
      setSelectState={setSelectListState}
      rules={[
        {
          required: true,
          message: '请选择所属单位！',
        },
      ]}
    />{' '}
  </>
);

const Subcontractor: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const createFormRef = useRef<ProFormInstance<API.Subcontractor>>();
  const modifyFormRef = useRef<ProFormInstance<API.Subcontractor>>();
  const [selectListState, setSelectListState] = useState<API.SelectList[]>([]);

  const submitPreHandler = (formData: API.Subcontractor) => {
    formData.phoneNumbers = formData.phoneNumbers?.map((value: { phoneNumber: string }) =>
      submitPrePhoneNumberHandle(value.phoneNumber),
    );
    formData.positions = formData.positions?.map(
      (position: { position: string }) => position.position,
    );
    formData.titles = formData.titles?.map((title: { title: string }) => title.title);
    formData.companyId = formData.companyId[formData.companyId.length - 1];
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
          path: '/company/subcontractor',
          breadcrumbName: '社区分包人员列表',
        },
      ]}
    >
      {' '}
      <DataList<API.Subcontractor, API.Subcontractor>
        customActionRef={actionRef}
        customRequest={async (params) => await querySubcontractorList(params)}
        customColumns={[
          {
            title: '社区分包人员姓名',
            dataIndex: 'name',
            sorter: true,
            ellipsis: true,
          },
          {
            title: '社区分包人员所属单位',
            dataIndex: ['company', 'name'],
            ellipsis: true,
            sorter: true,
            renderFormItem() {
              return (
                <SelectCascder
                  selectState={selectListState}
                  setSelectState={setSelectListState}
                  querySelectList={async (value) => (await queryCompanySelectList([value])).data}
                  cascaderFieldProps={{
                    multiple: true,
                  }}
                />
              );
            },
          },
          {
            title: '联系方式',
            dataIndex: 'phoneNumbers',
            hideInTable: true,
          },
        ]}
        batchRemoveEventHandler={async (data) =>
          await batchSubcontractor({
            method: 'DELETE',
            data,
          })
        }
        createEditModalForm={{
          element: InputElement(selectListState, setSelectListState),
          props: {
            title: '添加社区分包人员',
          },
          formRef: createFormRef,
          onFinish: async (formData) => await createSubcontractor(submitPreHandler(formData)),
        }}
        modifyEditModalForm={{
          element: InputElement(selectListState, setSelectListState),
          props: {
            title: '编辑社区分包人员',
          },
          formRef: modifyFormRef,
          onFinish: async (formData) =>
            await modifySubcontractor(formData.id, submitPreHandler(formData)),
          onConfirmRemove: async (id) => await removeSubcontractor(id),
          queryData: async (id) => {
            const result = await querySubcontractor(id);
            result.data.positions = result.data.positions?.map(
              (position: { position: string }) => ({ position }),
            );
            result.data.titles = result.data.titles?.map((title: { title: string }) => ({ title }));
            return result;
          },
        }}
        onLoad={async () => {
          let list: API.SelectList[] = [
            {
              label: '无',
              title: '无',
              value: '0',
            },
          ];
          const parentIds = getCompanyParentIds();
          const companySelect = (await queryCompanySelectList(parentIds)).data;
          if (companySelect.length > 0) {
            list = [...list, ...companySelect];
          }
          setSelectListState(list);
        }}
      />{' '}
    </MainPageContainer>
  );
};

export default Subcontractor;
