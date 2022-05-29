declare module '*.less';
declare namespace API {
  import type { FieldTypeEnum } from '@/services/enums';

  type Company = BaseEntity & {
    name?: string;
    actualNumber?: number;
    phoneNumbers?: PhoneNumber[];
    parentId?: number;
    parentIdCascder?: number[];
    children?: Company[];
    systemPermissions?: SystemPermission[];
    systemPermissionSelectList?: SelectList[];
    companyExtras?: CompanyExtra[];
  };

  type CompanyExtra = BaseEntity & {
    title?: string;
    description?: string;
    name?: string;
    content?: string;
    fieldType?: FieldTypeEnum;
    companyId?: number;
  };
}
