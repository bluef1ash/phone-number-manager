import {
  DownloadOutlined,
  FileExcelOutlined,
  PlusOutlined,
  UploadOutlined,
} from '@ant-design/icons';
import type { ProFormInstance } from '@ant-design/pro-form';
import type { ProFormProps } from '@ant-design/pro-form/lib/layouts/ProForm';
import type { ParamsType } from '@ant-design/pro-provider';
import type { ActionType, ProTableProps } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import type { ProColumns } from '@ant-design/pro-table/lib/typing';
import { LinearProgress } from '@mui/material';
import type { ModalProps, UploadProps } from 'antd';
import { Button, message as msg, notification, Popconfirm, Progress, Space, Upload } from 'antd';
import type { Key, SortOrder } from 'antd/lib/table/interface';
import type { RcFile } from 'antd/lib/upload';
import { cloneDeep, isArray } from 'lodash';
import React, { useState } from 'react';
import EditModalForm from './EditModalForm';
import styles from './index.less';

export type DataListProps<T extends API.BaseEntity, U extends ParamsType> = {
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
  batchElement?: (selectedRowKeys: number[]) => JSX.Element;
  importDataUploadProps?: {
    customAction?: (
      params: string | RcFile | Blob,
    ) => Promise<API.ResponseSuccess | API.ResponseException>;
  } & UploadProps;
  exportDataEventHandler?: React.MouseEventHandler<HTMLElement>;
  removeData?: (id: number) => Promise<API.ResponseSuccess | API.ResponseException>;
  modalForm?: {
    title?: string;
    element?: JSX.Element;
    props?: ProFormProps<T>;
    modalProps?: ModalProps;
    onCreateFinish?: (formData: T) => Promise<API.ResponseSuccess | API.ResponseException>;
    onModifyFinish?: (formData: T) => Promise<API.ResponseSuccess | API.ResponseException>;
    queryData?: (id: number) => Promise<API.Data<T> & API.ResponseException>;
    formRef?: React.MutableRefObject<ProFormInstance<T> | undefined>;
    onCancel?: (e?: React.MouseEvent<HTMLElement>) => void;
    createButtonPreHandler?: (e: React.MouseEvent<HTMLElement>) => void;
    modifyButtonPreHandler?: (e: React.MouseEvent<HTMLElement>) => void;
  };
} & Omit<ProTableProps<T, U>, 'children'>;

function DataList<T extends API.BaseEntity, U extends ParamsType>({
  customActionRef,
  customColumns,
  customRequest,
  batchRemoveEventHandler,
  batchElement,
  importDataUploadProps,
  exportDataEventHandler,
  removeData,
  modalForm,
  ...restProps
}: DataListProps<T, U>) {
  const [selectedRowKeysState, setSelectedRowKeys] = useState<Key[]>([]);
  const [modalVisibleState, setModalVisibleState] = useState<boolean>(false);
  const [modalLoadingState, setModalLoadingState] = useState<boolean>(false);
  const [modalCreateState, setModalCreateState] = useState<boolean>(false);

  const modalHandleCancel = () => {
    modalForm?.formRef?.current?.resetFields();
  };

  const modalHandleOk = () => {
    setModalLoadingState(true);
    modalForm?.formRef?.current?.submit();
  };

  return (
    <>
      <ProTable<T, U>
        actionRef={customActionRef}
        columnsState={{
          persistenceKey: 'dataList',
          persistenceType: 'localStorage',
        }}
        columns={[
          ...(customColumns || []),
          {
            title: '操作',
            valueType: 'option',
            render: (node, { id }) => [
              <a
                key={'edit_button' + id.toString()}
                onClick={async (e) => {
                  if (
                    typeof modalForm === 'undefined' ||
                    typeof modalForm.queryData === 'undefined'
                  ) {
                    return;
                  }
                  const { code, data } = await modalForm.queryData(id);
                  if (code === 200 && typeof data !== 'undefined') {
                    modalForm?.formRef?.current?.setFieldsValue(data);
                  }
                  setModalCreateState(false);
                  setModalVisibleState(true);
                  modalForm?.modifyButtonPreHandler?.(e);
                }}
              >
                {' '}
                编辑{' '}
              </a>,
              <Popconfirm
                title="是否真的要删除此条目？"
                onConfirm={async () => {
                  if (typeof removeData === 'undefined') {
                    return;
                  }
                  const { code, message } = await removeData(id);
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
        ]}
        request={async (params, sort) => {
          const { current, pageSize } = params;
          const searchParams = cloneDeep(params);
          delete searchParams.current;
          delete searchParams.pageSize;
          const otherParams: { search?: ParamsType; sort?: Record<string, SortOrder> } = {};
          if (Object.keys(searchParams).length > 0) {
            const search: ParamsType = {};
            for (const searchParam in searchParams) {
              if (
                typeof searchParams[searchParam] !== 'object' ||
                isArray(searchParams[searchParam])
              ) {
                search[searchParam] = searchParams[searchParam];
              } else {
                for (const searchParamKey in searchParams[searchParam]) {
                  search[searchParam + '.' + searchParamKey] = [];
                  if (isArray(searchParams[searchParam][searchParamKey])) {
                    for (const searchParamElementKey in searchParams[searchParam][searchParamKey]) {
                      search[searchParam + '.' + searchParamKey].push(
                        searchParams[searchParam][searchParamKey][searchParamElementKey][
                          searchParams[searchParam][searchParamKey][searchParamElementKey].length -
                            1
                        ],
                      );
                    }
                  }
                }
              }
            }
            otherParams.search = search;
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
        form={{
          syncToUrl: false,
        }}
        dateFormatter="string"
        toolBarRender={() => [
          ((): JSX.Element => {
            if (typeof importDataUploadProps !== 'undefined') {
              return (
                <Upload
                  multiple={false}
                  maxCount={1}
                  customRequest={async ({ file, onSuccess, onError }) => {
                    const result = await importDataUploadProps?.customAction?.(file);
                    if (result?.code === 200) {
                      onSuccess?.(result);
                    } else {
                      onError?.({
                        status: result?.code || 400,
                        method: 'post',
                        message: result?.message || '上传失败！',
                        name: '',
                      });
                    }
                  }}
                  beforeUpload={(file) => {
                    const isXlsx =
                      file.type ===
                      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
                    if (!isXlsx) {
                      notification.error({
                        key: 'import_data_notification',
                        message: '只能上传Excel文件！',
                        description: '',
                        duration: 4.5,
                      });
                    }
                    return isXlsx || Upload.LIST_IGNORE;
                  }}
                  onChange={async ({ file }) => {
                    switch (file.status) {
                      case 'uploading':
                        notification.info({
                          key: 'import_data_notification',
                          message: '正在上传文件，请稍后...',
                          description: (
                            <div>
                              <h5>
                                <FileExcelOutlined /> {file.name}
                              </h5>
                              <LinearProgress />
                            </div>
                          ),
                          duration: 0,
                        });
                        break;
                      case 'error':
                        notification.error({
                          key: 'import_data_notification',
                          message: '上传文件失败！',
                          description: (
                            <div>
                              <h5>
                                <FileExcelOutlined /> {file.name}
                              </h5>
                              <Progress percent={file.percent || 100} status="exception" />
                            </div>
                          ),
                          duration: 4.5,
                        });
                        break;
                      case 'done':
                        notification.success({
                          key: 'import_data_notification',
                          message: '上传文件成功！',
                          description: (
                            <div>
                              <h5>
                                <FileExcelOutlined /> {file.name}
                              </h5>
                              <Progress percent={file.percent || 100} />
                            </div>
                          ),
                          duration: 4.5,
                        });
                        customActionRef?.current?.reload();
                        break;
                      case 'success':
                        break;
                      case 'removed':
                        break;
                      case undefined:
                        throw new Error('Not implemented yet: undefined case');
                    }
                  }}
                  showUploadList={false}
                  {...importDataUploadProps}
                >
                  {' '}
                  <Button key="importDataButton" icon={<UploadOutlined />}>
                    {' '}
                    导入数据{' '}
                  </Button>{' '}
                </Upload>
              );
            }
            return <></>;
          })(),
          ((): JSX.Element => {
            if (typeof exportDataEventHandler !== 'undefined') {
              return (
                <Button
                  key="exportDataButton"
                  icon={<DownloadOutlined />}
                  onClick={exportDataEventHandler}
                >
                  {' '}
                  导出数据{' '}
                </Button>
              );
            }
            return <></>;
          })(),
          <Button
            key="createDataButton"
            icon={<PlusOutlined />}
            type="primary"
            onClick={(e) => {
              modalForm?.formRef?.current?.resetFields();
              setModalCreateState(true);
              setModalVisibleState(true);
              modalForm?.createButtonPreHandler?.(e);
            }}
          >
            {' '}
            添加{' '}
          </Button>,
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
            {((): JSX.Element => {
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
              } else {
                return <></>;
              }
            })()}{' '}
            {batchElement?.(selectedRowKeys as number[])}
          </Space>
        )}
        {...restProps}
      />{' '}
      <EditModalForm
        modalProps={{
          title: (modalCreateState ? '添加' : '编辑') + modalForm?.title,
          visible: modalVisibleState,
          confirmLoading: modalLoadingState,
          onOk: modalHandleOk,
          onCancel: (e) => {
            modalForm?.onCancel?.(e);
            modalForm?.formRef?.current?.resetFields();
            setModalVisibleState(false);
          },
          footer: (
            <>
              {modalCreateState ? (
                <></>
              ) : (
                <span className={styles['modify-time']}>
                  更新时间：{modalForm?.formRef?.current?.getFieldValue('updateTime')}{' '}
                </span>
              )}{' '}
              <Button key="cancel" onClick={modalHandleCancel}>
                {' '}
                重置{' '}
              </Button>{' '}
              <Button key="ok" type="primary" onClick={modalHandleOk} loading={modalLoadingState}>
                {' '}
                保存{' '}
              </Button>
            </>
          ),
          ...modalForm?.modalProps,
        }}
        onFinish={async (formData) => {
          if (
            typeof modalForm === 'undefined' ||
            typeof modalForm.onCreateFinish === 'undefined' ||
            typeof modalForm.onModifyFinish === 'undefined'
          ) {
            return false;
          }
          formData.id = modalForm?.formRef?.current?.getFieldValue('id');
          const { code, message } = modalCreateState
            ? await modalForm.onCreateFinish(formData)
            : await modalForm.onModifyFinish(formData);
          if (code === 200) {
            msg.success(
              message === 'success' ? `${modalCreateState ? '添加' : '修改'}成功！` : message,
            );
            setModalLoadingState(false);
            customActionRef?.current?.reload();
            setModalVisibleState(false);
            return true;
          }
          setModalLoadingState(false);
          msg.error(message);
          return false;
        }}
        formRef={modalForm?.formRef}
        submitter={{
          render: false,
        }}
        {...modalForm?.props}
      >
        {modalForm?.element}
      </EditModalForm>
    </>
  );
}

export default DataList;
