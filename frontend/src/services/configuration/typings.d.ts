declare module '*.less';
declare namespace API {
  import type { FieldTypeEnum } from '@/services/enums';

  type Configuration = BaseEntity & {
    title: string;
    description: string;
    name: string;
    content: string;
    fieldType: string;
    fieldValue: FieldTypeEnum;
    orderBy: number;
  };
}
