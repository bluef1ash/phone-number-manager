import React from 'react';
import type { LightFilterFooterRender, ProFormItemProps } from '@ant-design/pro-form';
import { ProFormCascader } from '@ant-design/pro-form';
import { querySystemUserSelectList } from '@/services/user/api';
import type { CascaderProps } from 'antd';
import type { ExtendsProps, ProFormFieldRemoteProps } from '@ant-design/pro-form/lib/interface';
import type { FieldProps } from '@ant-design/pro-form/es/interface';
import type { ProFieldProps } from '@ant-design/pro-utils/lib';

export type SubcontractorProps = {
  fieldProps?: (FieldProps & CascaderProps<any>) | undefined;
  placeholder?: string | string[] | undefined;
  secondary?: boolean | undefined;
  cacheForSwr?: boolean | undefined;
  disabled?: boolean | undefined;
  width?: number | 'sm' | 'md' | 'xl' | 'xs' | 'lg' | undefined;
  proFieldProps?: ProFieldProps | undefined;
  footerRender?: LightFilterFooterRender | undefined;
  systemUsersSelectState: API.SelectList[];
  setSystemUsersSelectState: React.Dispatch<React.SetStateAction<API.SelectList[]>>;
  requiredMessage?: string;
  cascaderFieldProps?: ProFieldProps & CascaderProps<any>;
} & Omit<ProFormItemProps, 'valueType'> &
  ExtendsProps &
  ProFormFieldRemoteProps &
  React.RefAttributes<any>;

const Subcontractor: React.FC<SubcontractorProps> = ({
  systemUsersSelectState,
  setSystemUsersSelectState,
  requiredMessage,
  cascaderFieldProps,
  ...restProps
}) => (
  <ProFormCascader
    width="xl"
    placeholder="请选择"
    fieldProps={{
      options: systemUsersSelectState,
      async loadData(selectedOptions) {
        const targetOption = selectedOptions[selectedOptions.length - 1];
        targetOption.loading = true;
        const data = (await querySystemUserSelectList(targetOption.value as number)).data;
        targetOption.loading = false;
        //@ts-ignore
        targetOption.children = data;
        setSystemUsersSelectState([...systemUsersSelectState]);
      },
      ...cascaderFieldProps,
    }}
    rules={[
      typeof requiredMessage === 'undefined'
        ? {
            required: true,
            message: requiredMessage,
          }
        : {},
    ]}
    {...restProps}
  />
);

export default Subcontractor;
