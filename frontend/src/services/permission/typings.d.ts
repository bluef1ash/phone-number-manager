declare module '*.less';
declare namespace API {
  import type { MenuDataItem } from '@ant-design/pro-layout';
  import type { HttpMethod, MenuTypeEnum } from '@/services/enums';

  type ResponseMenu = {
    code: number;
    menuData?: MenuDataItem[];
    components?: string[];
  };

  type SystemPermission = {
    id?: number;
    name?: string;
    functionName?: string;
    uri?: string;
    httpMethods?: HttpMethod[] | { httpMethod: HttpMethod }[];
    level?: number;
    parentId?: number;
    iconName?: string;
    orderBy?: number;
    menuType?: MenuTypeEnum;
    isDisplay?: boolean;
    children?: SystemPermission[];
  };
}
