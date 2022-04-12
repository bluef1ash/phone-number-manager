import React from 'react';
import type { LightFilterFooterRender, ProFormItemProps } from '@ant-design/pro-form';
import { ProFormCascader } from '@ant-design/pro-form';
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
  selectState: API.SelectList[];
  setSelectState: React.Dispatch<React.SetStateAction<API.SelectList[]>>;
  requiredMessage?: string;
  cascaderFieldProps?: ProFieldProps & CascaderProps<any>;
  querySelectList: (value: number) => Promise<API.SelectList[]>;
} & Omit<ProFormItemProps, 'valueType'> &
  ExtendsProps &
  ProFormFieldRemoteProps &
  React.RefAttributes<any>;

const SelectCascder: React.FC<SubcontractorProps> = ({
  selectState,
  setSelectState,
  requiredMessage,
  cascaderFieldProps,
  querySelectList,
  ...restProps
}) => (
  <ProFormCascader
    width="xl"
    placeholder="请选择"
    fieldProps={{
      options: selectState,
      async loadData(selectedOptions) {
        const targetOption = selectedOptions[selectedOptions.length - 1];
        targetOption.loading = true;
        //@ts-ignore
        targetOption.children = await querySelectList(targetOption.value as number);
        targetOption.loading = false;
        setSelectState([...selectState]);
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

export default SelectCascder;
