import React from 'react';
import type { ProFormProps } from '@ant-design/pro-form';
import { ProFormSwitch, ProFormText } from '@ant-design/pro-form';
import SelectCascder from '@/components/SelectCascder';
import { queryCompanySelectList } from '@/services/company/api';
import type { RuleObject, StoreValue } from 'rc-field-form/lib/interface';

export type UserFormProps = {
  companySelectList: API.SelectList[];
  setCompanySelectList: React.Dispatch<React.SetStateAction<API.SelectList[]>>;
  isCreate: boolean;
} & ProFormProps;

export const UserFormSubmitPreHandler = (formData: API.SystemUser) => {
  if (typeof formData.companyIds !== 'undefined') {
    formData.companies = formData.companyIds.map((id: number[]) => ({ id: id[id.length - 1] }));
  }
  return formData;
};

const UserForm: React.FC<UserFormProps> = ({
  companySelectList,
  setCompanySelectList,
  isCreate,
}) => (
  <>
    <ProFormText
      width="xl"
      name="username"
      label="系统用户名称"
      tooltip="不能超过10个字符"
      placeholder="请输入系统用户名称"
      disabled={!isCreate}
      rules={[
        {
          required: true,
          message: '系统用户名称不能为空！',
        },
        {
          max: 30,
          message: '系统用户名称不能超过30个字符！',
        },
      ]}
    />{' '}
    <ProFormText.Password
      width="xl"
      name="password"
      label="系统用户密码"
      tooltip="不能少于6个字符，修改时不填写则不修改密码"
      placeholder="请输入系统用户密码"
      rules={[
        isCreate
          ? {
              required: true,
              message: '系统用户密码不能为空！',
            }
          : {},
        {
          min: 6,
          message: '系统用户密码不能少于6个字符！',
        },
      ]}
    />{' '}
    <ProFormText.Password
      width="xl"
      name="confirmPassword"
      label="确认系统用户密码"
      tooltip="不能少于6个字符"
      placeholder="请再次输入系统用户密码"
      dependencies={['password']}
      rules={[
        isCreate
          ? {
              required: true,
              message: '确认系统用户密码不能为空！',
            }
          : {},
        {
          min: 6,
          message: '系统用户密码不能少于6个字符！',
        },
        isCreate
          ? ({ getFieldValue }) => ({
              validator: (rules, value) =>
                getFieldValue('password') === value
                  ? Promise.resolve()
                  : Promise.reject('两次密码输入不一致'),
            })
          : {},
      ]}
    />{' '}
    <ProFormSwitch name="isLocked" label="系统用户是否被锁定" initialValue={false} />{' '}
    <ProFormSwitch name="isEnabled" label="系统用户是否启用" initialValue={true} />{' '}
    <SelectCascder
      width="xl"
      name="companyIds"
      label="系统用户所属单位"
      querySelectList={async (value) => (await queryCompanySelectList([value])).data}
      selectState={companySelectList}
      setSelectState={setCompanySelectList}
      rules={[
        {
          required: true,
          message: '请选择系统用户所属单位！',
        },
        {
          validator(rule: RuleObject, value: StoreValue) {
            return value.length > 1 && value.some((item: any[]) => item[0] === 0)
              ? Promise.reject('所属单位不能同时选择单位和无单位！')
              : Promise.resolve();
          },
        },
      ]}
      cascaderFieldProps={{
        multiple: true,
      }}
    />{' '}
  </>
);

export default UserForm;
