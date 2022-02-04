declare module '*.less';
declare namespace API {
  import type { MenuDataItem } from '@ant-design/pro-layout';

  type ResponseMenu = {
    code: number;
    menuData?: MenuDataItem[];
    components?: string[];
  };

  enum HttpMethod {
    GET = 'GET',
    HEAD = 'HEAD',
    POST = 'POST',
    PUT = 'PUT',
    PATCH = 'PATCH',
    DELETE = 'DELETE',
    OPTIONS = 'OPTIONS',
    TRACE = 'TRACE',
  }

  enum MenuTypeEnum {
    ALL = 'ALL',
    FRONTEND = 'FRONTEND',
    BACKEND = 'BACKEND',
  }

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
