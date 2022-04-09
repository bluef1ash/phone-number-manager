import React from 'react';
import type { ProFormProps } from '@ant-design/pro-form';
import { ProFormList, ProFormRadio, ProFormSwitch, ProFormText } from '@ant-design/pro-form';
import CompanyTreeSelect from '@/components/UserForm/CompanyTreeSelect';
import { submitPrePhoneNumberHandle } from '@/services/utils';
import { isArray } from 'lodash';

export type UserFormProps = {
  companySelectList: API.SelectList[];
} & ProFormProps;

export const UserFormSubmitPreHandler = (formData: API.SystemUser) => {
  if (formData.phoneNumber != null && formData.phoneNumber.phoneNumber != null) {
    formData.phoneNumber = submitPrePhoneNumberHandle(formData.phoneNumber.phoneNumber);
  }
  if (typeof formData.companyIds !== 'undefined') {
    formData.companies = formData.companyIds.map((id) => ({ id }));
  }
  if (isArray(formData.positions) && formData.positions.length > 0) {
    formData.positions = formData.positions?.map(
      (position) => (position as { position: string }).position,
    );
  }
  if (isArray(formData.titles) && formData.titles.length > 0) {
    formData.titles = formData.titles?.map((title) => (title as { title: string }).title);
  }
  return formData;
};

const UserForm: React.FC<UserFormProps> = ({ companySelectList }) => {
  return (
    <>
      <ProFormText
        width="xl"
        name={['phoneNumber', 'phoneNumber']}
        label="系统用户联系方式"
        tooltip="只支持手机号码"
        placeholder="请输入系统用户联系方式"
        rules={[
          {
            required: true,
            message: '系统用户联系方式不能为空！',
          },
          {
            pattern: /(13\d|14[579]|15[^4\D]|17[^49\D]|18\d)\d{8}/,
            message: '系统用户联系方式输入不正确！',
          },
        ]}
      />{' '}
      <ProFormText
        width="xl"
        name="username"
        label="系统用户姓名"
        tooltip="不能超过10个字符"
        placeholder="请输入系统用户姓名"
        rules={[
          {
            required: true,
            message: '系统用户姓名不能为空！',
          },
          {
            max: 10,
            message: '系统用户名称不能超过100个字符！',
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
          {
            min: 6,
            message: '系统用户密码不能少于6个字符！',
          },
          ({ getFieldValue }) => ({
            validator: (rules, value) =>
              getFieldValue('password') === value
                ? Promise.resolve()
                : Promise.reject('两次密码输入不一致'),
          }),
        ]}
      />{' '}
      <ProFormList
        name="positions"
        creatorButtonProps={{
          creatorButtonText: '添加系统用户职务',
        }}
      >
        {' '}
        <ProFormText
          width="xl"
          name="position"
          label="系统用户职务"
          tooltip="不能超过10个字符"
          placeholder="请输入系统用户职务"
          rules={[
            {
              max: 10,
              message: '系统用户职务不能超过10个字符！',
            },
          ]}
        />{' '}
      </ProFormList>{' '}
      <ProFormList
        name="titles"
        creatorButtonProps={{
          creatorButtonText: '添加系统用户职称',
        }}
      >
        {' '}
        <ProFormText
          width="xl"
          name="title"
          label="系统用户职称"
          tooltip="不能超过10个字符"
          placeholder="请输入系统用户职称"
          rules={[
            {
              max: 10,
              message: '系统用户职称不能超过10个字符！',
            },
          ]}
        />{' '}
      </ProFormList>{' '}
      <ProFormRadio.Group
        name="isSubcontract"
        label="是否参加社区分包"
        initialValue={false}
        options={[
          {
            label: '是',
            value: true,
          },
          {
            label: '否',
            value: false,
          },
        ]}
      />{' '}
      <ProFormSwitch name="isLocked" label="系统用户是否被锁定" initialValue={false} />{' '}
      <ProFormSwitch name="isEnabled" label="系统用户是否启用" initialValue={true} />{' '}
      <CompanyTreeSelect companySelectList={companySelectList} isRules={true} />
    </>
  );
};

export default UserForm;
