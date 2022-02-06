import React, { useState } from 'react';
import type { ActionType, ProTableProps } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { Button, message as msg, Popconfirm, Space } from 'antd';
import type { ParamsType } from '@ant-design/pro-provider';
import type { EditModalFormProps } from './EditModalForm';
import EditModalForm from './EditModalForm';
import { PlusOutlined } from '@ant-design/icons';
import type { ProFormInstance } from '@ant-design/pro-form';
import type { ProColumns } from '@ant-design/pro-table/lib/typing';
import type { SortOrder } from 'antd/es/table/interface';
import type { Key } from 'antd/lib/table/interface';

type DataListProps<T, U extends ParamsType> = {
  customActionRef?: React.MutableRefObject<ActionType | undefined>;
  customColumns?: ProColumns<T>[];
  customRequest?: (responseParams: {
    current?: number;
    pageSize?: number;
    params?: string;
  }) => Promise<API.DataList<T> & API.ResponseException>;
  batchRemoveEventHandler?: (
    selectedRowKeys: number[],
  ) => Promise<API.ResponseSuccess | API.ResponseException>;
  exportDataEventHandler?: React.MouseEventHandler<HTMLElement>;
  createEditModalForm?: {
    element?: JSX.Element;
    props?: EditModalFormProps<T>;
    formRef?: React.MutableRefObject<ProFormInstance<T> | undefined>;
    onFinish?: (formData: T) => Promise<API.ResponseSuccess | API.ResponseException>;
  };
  modifyEditModalForm?: {
    element?: JSX.Element;
    props?: EditModalFormProps<T>;
    onFinish?: (formData: T) => Promise<API.ResponseSuccess | API.ResponseException>;
    onConfirmRemove?: (id: number) => Promise<API.ResponseSuccess | API.ResponseException>;
    queryData?: (id: number) => Promise<API.Data<T> & API.ResponseException>;
    formRef?: React.MutableRefObject<ProFormInstance<T> | undefined>;
  };
} & ProTableProps<T, U>;

function DataList<T, U extends ParamsType>({
  customActionRef,
  customColumns,
  customRequest,
  batchRemoveEventHandler,
  exportDataEventHandler,
  createEditModalForm,
  modifyEditModalForm,
  ...restProps
}: DataListProps<T, U>): JSX.Element {
  const [selectedRowKeysState, setSelectedRowKeys] = useState<Key[]>([]);
  const [createEditModalFormVisible, setCreateEditModalFormVisible] = useState<boolean | undefined>(
    undefined,
  );
  const [modifyEditModalFormVisible] = useState<boolean | undefined>(undefined);
  const customFormColumns: ProColumns<T>[] = [
    ...(customColumns || []),
    {
      title: '操作',
      valueType: 'option',
      //@ts-ignore
      render: (node, { id }: T) => [
        <EditModalForm<T>
          key={'edit_modal' + id.toString()}
          trigger={<a key={'edit_button' + id.toString()}> 编辑 </a>}
          width={600}
          submitter={{
            searchConfig: {
              submitText: '保存',
              resetText: '重置',
            },
            resetButtonProps: {
              onClick: () => modifyEditModalForm?.formRef?.current?.resetFields(),
            },
          }}
          onFinish={async (formData) => {
            //@ts-ignore
            formData.id = id;
            //@ts-ignore
            const { code, message } = await modifyEditModalForm?.onFinish?.(formData);
            if (code === 200) {
              msg.success(message === 'success' ? '修改成功！' : message);
              customActionRef?.current?.reload();
              return true;
            }
            msg.error(message);
            return false;
          }}
          onVisibleChange={async (visible) => {
            if (visible) {
              //@ts-ignore
              const { code, data } = await modifyEditModalForm?.queryData?.(id);
              if (code === 200) {
                modifyEditModalForm?.formRef?.current?.setFieldsValue(data);
              }
            }
          }}
          visible={modifyEditModalFormVisible}
          formRef={modifyEditModalForm?.formRef}
          {...modifyEditModalForm?.props}
        >
          {modifyEditModalForm?.element}
        </EditModalForm>,
        <Popconfirm
          title="是否真的要删除此条目？"
          onConfirm={async () => {
            //@ts-ignore
            const { code, message } = await modifyEditModalForm?.onConfirmRemove?.(id);
            if (code === 200) {
              msg.success(message === 'success' ? '删除成功！' : message);
              customActionRef?.current?.reload();
            } else {
              msg.error(message);
            }
          }}
          okText="是"
          cancelText="否"
          key={'remove_confirm' + id}
        >
          {' '}
          <a key={'remove_button' + id}>删除</a>{' '}
        </Popconfirm>,
      ],
    },
  ];
  return (
    <ProTable<T, U>
      actionRef={customActionRef}
      columnsState={{
        persistenceKey: 'pro-table-singe-demos',
        persistenceType: 'localStorage',
      }}
      columns={[...customFormColumns]}
      request={async (params, sort) => {
        const { current, pageSize } = params;
        const searchParams = Object.assign({}, params);
        delete searchParams.current;
        delete searchParams.pageSize;
        const otherParams: { search?: U; sort?: Record<string, SortOrder> } = {};
        if (Object.keys(searchParams).length > 0) {
          otherParams.search = searchParams;
        }
        if (Object.keys(sort).length > 0) {
          otherParams.sort = sort;
        }
        const responseParams: { current?: number; pageSize?: number; params?: string } = {
          current,
          pageSize,
        };
        if (Object.keys(otherParams).length > 0) {
          responseParams.params = JSON.stringify(otherParams);
        }
        const result = await customRequest?.(responseParams);
        return {
          data: result?.data.records,
          success: result?.code === 200,
          total: result?.data.total,
        };
      }}
      rowKey="id"
      search={{
        labelWidth: 'auto',
      }}
      pagination={{
        pageSize: 10,
      }}
      form={{
        syncToUrl: false,
      }}
      dateFormatter="string"
      toolBarRender={() => [
        <EditModalForm<T>
          key="createData"
          trigger={
            <Button key="createDataButton" icon={<PlusOutlined />} type="primary">
              {' '}
              添加{' '}
            </Button>
          }
          submitter={{
            searchConfig: {
              submitText: '保存',
              resetText: '重置',
            },
            resetButtonProps: {
              onClick: () => createEditModalForm?.formRef?.current?.resetFields(),
            },
          }}
          width={600}
          onFinish={async (formData) => {
            //@ts-ignore
            const { code, message } = await createEditModalForm?.onFinish?.(formData);
            if (code === 200) {
              setCreateEditModalFormVisible(false);
              msg.success(message === 'success' ? '添加成功！' : message);
              customActionRef?.current?.reload();
            } else {
              msg.error(message);
            }
          }}
          visible={createEditModalFormVisible}
          formRef={createEditModalForm?.formRef}
          {...createEditModalForm?.props}
        >
          {createEditModalForm?.element}
        </EditModalForm>,
      ]}
      rowSelection={{
        selectedRowKeys: selectedRowKeysState,
        onChange(selectedRowKeys) {
          setSelectedRowKeys(selectedRowKeys);
        },
        checkStrictly: false,
      }}
      tableAlertRender={({ selectedRowKeys }) => (
        <Space size={24}>
          {' '}
          <span>已选 {selectedRowKeys.length} 项</span>{' '}
        </Space>
      )}
      tableAlertOptionRender={({ selectedRowKeys }) => (
        <Space size={16}>
          {' '}
          {((): JSX.Element | void => {
            if (typeof batchRemoveEventHandler !== 'undefined') {
              return (
                <Popconfirm
                  title="是否真的要批量删除条目？"
                  onConfirm={async () => {
                    const { code, message } = await batchRemoveEventHandler(
                      selectedRowKeys as number[],
                    );
                    if (code === 200) {
                      setSelectedRowKeys([]);
                      msg.success(message === 'success' ? '批量删除成功！' : message);
                      customActionRef?.current?.reload();
                    } else {
                      msg.error(message);
                    }
                  }}
                  okText="是"
                  cancelText="否"
                  key="batch_remove_confirm"
                >
                  {' '}
                  <a key="batch_remove_button">批量删除</a>{' '}
                </Popconfirm>
              );
            }
          })()}{' '}
          {((): JSX.Element | void => {
            if (typeof exportDataEventHandler !== 'undefined') {
              return <a onClick={exportDataEventHandler}>导出数据</a>;
            }
          })()}{' '}
        </Space>
      )}
      {...restProps}
    />
  );
}

export default DataList;
