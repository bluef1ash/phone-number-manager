import type { ProFormProps} from '@ant-design/pro-form';
import { ProFormTreeSelect } from '@ant-design/pro-form';
import React from 'react';

export type CompanyTreeSelectProps = {
  isRules: boolean;
  companySelectList: API.SelectList[];
} & ProFormProps;

const CompanyTreeSelect: React.FC<CompanyTreeSelectProps> = ({ isRules, companySelectList }) => {
  const rules = [];
  let label = '';
  if (isRules) {
    rules.push({
      required: true,
      message: '至少选择一个所属单位！',
    });
    label = '系统用户所属单位';
  }
  return (
    <ProFormTreeSelect
      name="companyIds"
      label={label}
      placeholder="请选择"
      allowClear
      secondary
      request={async () => companySelectList}
      fieldProps={{
        multiple: true,
      }}
      rules={rules}
    />
  );
};

export default CompanyTreeSelect;
