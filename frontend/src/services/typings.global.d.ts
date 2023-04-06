declare module '*.less';

declare const REACT_APP_API_BASE_URL: string;

declare namespace API {
    import type { DefaultOptionType } from 'antd/lib/cascader';
    import type { ImportOrExportStatusEnum, PhoneNumberType } from '@/services/enums';

    type ResponseSuccess = {
        code: number;
        message: string;
    };

    type ResponseException = {
        code: number;
        defaultMessage?: string;
        message?: string;
    };

    type RecursivePartial<S extends object> = {
        [p in keyof S]+?: S[p] extends object ? RecursivePartial<S[p]> : S[p];
    };

    type BaseEntity = {
        id: number;
        createTime?: Date;
        updateTime?: Date;
        version?: number;
    } & RecursivePartial<BaseEntity>;

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

    type DataList<T> = ResponseSuccess & {
        data: DataListData<T>;
    };

    type Data<T> = ResponseSuccess & {
        data: T & BaseEntity;
    };

    type BatchRUD<T> = {
        method: 'CREATE' | 'MODIFY' | 'DELETE';
        data: T;
        extra?: any;
    };

    type SelectList = {
        label?: string;
        title?: string;
        value?: number;
        level?: number;
        isLeaf?: boolean;
        loading?: boolean;
        isSubordinate?: boolean;
        isGrandsonLevel?: boolean;
        children?: SelectList[] | null;
    } & DefaultOptionType;

    type NoticeIconItemType = 'notification' | 'message' | 'event';

    type NoticeIconItem = {
        id?: string;
        extra?: string;
        key?: string;
        read?: boolean;
        avatar?: string;
        title?: string;
        status?: string;
        datetime?: string;
        description?: string;
        type?: NoticeIconItemType;
    };

    type ImportFileProgress = ResponseSuccess & {
        status: ImportOrExportStatusEnum;
        importId: number;
    };

    type ExportFileProgress = ResponseSuccess & {
        status: ImportOrExportStatusEnum;
        exportId: number;
    };
}
