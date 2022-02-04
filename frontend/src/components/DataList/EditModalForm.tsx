import type { ModalFormProps } from '@ant-design/pro-form';
import { ModalForm } from '@ant-design/pro-form';

export type EditModalFormProps<T> = {} & ModalFormProps<T>;

function EditModalForm<T>({ ...restProps }: EditModalFormProps<T>): JSX.Element {
  return (
    <ModalForm<T>
      autoFocusFirstInput
      modalProps={{
        destroyOnClose: true,
        maskClosable: false,
      }}
      {...restProps}
    />
  );
}

export default EditModalForm;
