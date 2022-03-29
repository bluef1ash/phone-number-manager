declare module '*.less';
declare namespace API {
  type Company = BaseEntity & {
    name?: string;
    actualNumber?: number;
    level?: number;
    phoneNumbers?: PhoneNumber[];
    parentId?: number;
    children?: Company[];
    systemPermissions?: SystemPermission[];
    systemPermissionSelectList?: SelectList[];
  };
}
