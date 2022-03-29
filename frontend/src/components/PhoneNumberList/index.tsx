import React from 'react';
import type { RuleObject, StoreValue } from 'rc-field-form/lib/interface';
import { ProFormList, ProFormText } from '@ant-design/pro-form';
import { parsePhoneNumber } from 'libphonenumber-js/max';
import type { ProFormListProps } from '@ant-design/pro-form/lib/components/List';
import { isArray } from 'lodash';
import type { ProFormFieldItemProps } from '@ant-design/pro-form/lib/interface';

export type PhoneNumberListProps = {
  creatorButtonText?: string;
  listValidRejectMessage?: string;
  phoneNumberFormProps?: ProFormFieldItemProps;
} & Omit<ProFormListProps, 'children'>;

const PhoneNumberList: React.FC<PhoneNumberListProps> = ({
  creatorButtonText,
  listValidRejectMessage,
  phoneNumberFormProps,
  ...restProps
}) => (
  <ProFormList
    creatorButtonProps={{
      creatorButtonText: typeof creatorButtonText === 'undefined' ? '' : creatorButtonText,
    }}
    rules={[
      {
        validator(rule: RuleObject, value: StoreValue) {
          return typeof value !== 'undefined' && isArray(value)
            ? Promise.resolve()
            : Promise.reject(
                typeof listValidRejectMessage === 'undefined'
                  ? listValidRejectMessage
                  : '请添加联系方式！',
              );
        },
      },
    ]}
    {...restProps}
  >
    {' '}
    <ProFormText
      width="xl"
      rules={[
        {
          required: true,
          message: '社区居民联系方式不能为空！',
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
      {...phoneNumberFormProps}
    />{' '}
  </ProFormList>
);

export default PhoneNumberList;
