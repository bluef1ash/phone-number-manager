declare module '*.less';
declare namespace API {
  import type { NumberType } from 'libphonenumber-js/types';

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

  type PhoneNumber = {
    id?: number;
    phoneNumber?: string;
    phoneType?: NumberType;
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
    title?: string;
    value?: number;
    level?: number;
    children?: SelectList[] | null;
  };
}
