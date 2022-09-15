import SelectCascder from '@/components/SelectCascder';
import { querySubcontractorSelectList } from '@/services/subcontractor/api';
import type { ActionType } from '@ant-design/pro-table';
import { Button, message, Popover, Space } from 'antd';
import type { PopoverProps } from 'antd/lib/popover';
import type { MutableRefObject } from 'react';
import React from 'react';

export type BatchUpdateSubcontractorPopoverProps = {
  subcontractorSelectState: API.SelectList[];
  setSubcontractorSelectState: React.Dispatch<React.SetStateAction<API.SelectList[]>>;
  batchSetSubcontractorIdState: number;
  setBatchSetSubcontractorIdState: React.Dispatch<React.SetStateAction<number>>;
  selectedRowKeys: number[];
  actionRef: MutableRefObject<ActionType | undefined>;
  handler: (data: API.BatchRUD<any>) => Promise<API.ResponseSuccess & API.ResponseException>;
} & PopoverProps &
  React.RefAttributes<unknown>;

const BatchUpdateSubcontractorPopover: React.FC<BatchUpdateSubcontractorPopoverProps> = ({
  subcontractorSelectState,
  setSubcontractorSelectState,
  batchSetSubcontractorIdState,
  setBatchSetSubcontractorIdState,
  selectedRowKeys,
  actionRef,
  handler,
  ...restProps
}) => (
  <Popover
    trigger="click"
    content={
      <Space>
        {' '}
        <SelectCascder
          querySelectList={async (value) => (await querySubcontractorSelectList([value])).data}
          selectState={subcontractorSelectState}
          setSelectState={setSubcontractorSelectState}
          isNotProForm={true}
          cascaderFieldProps={{
            onChange(value: any[]) {
              if (value.length === 0) {
                setBatchSetSubcontractorIdState(0);
                return;
              }
              setBatchSetSubcontractorIdState(value[value.length - 1]);
            },
          }}
        />{' '}
        <Button
          key="batch_set_subcontractor_id"
          onClick={async () => {
            if (batchSetSubcontractorIdState === 0) {
              message.error('未选择修改的社区分包人员！');
              return;
            }
            const { code } = await handler({
              data: selectedRowKeys.map((id) => ({
                id,
                subcontractorId: batchSetSubcontractorIdState,
              })),
              method: 'MODIFY',
            });
            if (code === 200) {
              message.success('批量修改成功！');
              actionRef?.current?.reload();
              return;
            }
            message.error('批量修改失败！');
          }}
        >
          {' '}
          确定{' '}
        </Button>{' '}
      </Space>
    }
    {...restProps}
  >
    {' '}
    <a key="batch_change_subcontractor">批量修改社区分包人员</a>{' '}
  </Popover>
);

export default BatchUpdateSubcontractorPopover;
