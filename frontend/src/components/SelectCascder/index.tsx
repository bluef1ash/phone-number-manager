import React from 'react';
import type { LightFilterFooterRender, ProFormItemProps } from '@ant-design/pro-form';
import { ProFormCascader } from '@ant-design/pro-form';
import type { CascaderProps } from 'antd';
import { Cascader } from 'antd';
import type { ExtendsProps, ProFormFieldRemoteProps } from '@ant-design/pro-form/lib/interface';
import type { FieldProps } from '@ant-design/pro-form/es/interface';
import type { ProFieldProps } from '@ant-design/pro-utils/lib';
import type { DefaultOptionType } from 'antd/lib/cascader';

export type SelectCascderProps = {
  isNotProForm?: boolean;
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

const SelectCascder: React.FC<SelectCascderProps> = ({
  isNotProForm,
  selectState,
  setSelectState,
  requiredMessage,
  cascaderFieldProps,
  querySelectList,
  ...restProps
}) => {
  const loadData = async (selectedOptions: DefaultOptionType[]) => {
    const targetOption = selectedOptions[selectedOptions.length - 1];
    targetOption.loading = true;
    const value = targetOption.value as number;
    targetOption.children = await querySelectList(value);
    targetOption.loading = false;
    setSelectState([...selectState]);
  };
  return typeof isNotProForm !== 'undefined' && isNotProForm ? (
    <Cascader options={selectState} loadData={loadData} {...cascaderFieldProps} />
  ) : (
    <ProFormCascader
      width="xl"
      fieldProps={{
        options: selectState,
        loadData,
        ...cascaderFieldProps,
      }}
      rules={[
        typeof requiredMessage !== 'undefined'
          ? {
              required: true,
              message: requiredMessage,
            }
          : {},
      ]}
      {...restProps}
    />
  );
};

export default SelectCascder;
