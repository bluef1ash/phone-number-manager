declare module '*.less';

declare const REACT_APP_API_BASE_URL: string;

declare namespace API {
  import type { PhoneNumberType } from '@/services/enums';

  type ResponseSuccess = {
    code: number;
    message: string;
  };

  type ResponseException = {
    code: number;
    defaultMessage?: string;
    message?: string;
  };

  type PageParams = {
    current?: number;
    pageSize?: number;
    params?: [];
  };

  type BaseEntity = {
    id: number;
    createTime?: Date;
    updateTime?: Date;
    version?: number;
  };

  type PhoneNumber = {
    id?: number;
    phoneNumber?: string;
    phoneType?: PhoneNumberType;
  };

  type DataListData<T> = {
    records: T[];
    total: number;
    size: number;
    current: number;
    orders: number[];
    optimizeCountSql: boolean;
    searchCount: boolean;
    countId: number;
    maxLimit: number;
    pages: number;
  };

  type DataList<T> = {
    code: number;
    data: DataListData<T>;
    message: string;
  };

  type Data<T> = {
    code: number;
    data: T;
    message: string;
  };

  type BatchRUD<T> = {
    method: 'CREATE' | 'UPDATE' | 'DELETE';
    data: T;
  };

  type SelectList = {
    label?: string;
    title?: string;
    value?: number;
    level?: number;
    isLeaf?: boolean;
    loading?: boolean;
    children?: SelectList[] | null;
  } & DefaultOptionType;
}
