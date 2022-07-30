import { ProForm } from '@ant-design/pro-form';
import type { ModalProps } from 'antd';
import { Button, Modal } from 'antd';
import React from 'react';
import type { ProFormProps } from '@ant-design/pro-form/lib/layouts/ProForm';

export type EditModalFormProps<T extends API.BaseEntity> = {
  modalProps?: Omit<ModalProps, 'visible'>;
  modalHandleOk?: (e: React.MouseEvent<HTMLElement>) => void;
  modalHandleCancel?: (e: React.MouseEvent<HTMLElement>) => void;
} & ProFormProps<T>;

function EditModalForm<T extends API.BaseEntity>({
  modalProps,
  modalHandleOk,
  modalHandleCancel,
  ...restProps
}: EditModalFormProps<T>): JSX.Element {
  return (
    <Modal
      width={600}
      destroyOnClose={false}
      maskClosable={false}
      onOk={modalHandleOk}
      onCancel={modalHandleCancel}
      forceRender={true}
      footer={[
        <Button key="cancel" onClick={modalHandleCancel}>
          {' '}
          重置{' '}
        </Button>,
        <Button key="ok" type="primary" onClick={modalHandleOk}>
          {' '}
          保存{' '}
        </Button>,
      ]}
      {...modalProps}
    >
      {' '}
      <ProForm<T> autoFocusFirstInput={true} layout="vertical" {...restProps}>
        {restProps.children}
      </ProForm>{' '}
    </Modal>
  );
}

export default EditModalForm;
