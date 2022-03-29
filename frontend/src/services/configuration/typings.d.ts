declare module '*.less';
declare namespace API {
  type Configuration = BaseEntity & {
    title: string;
    description: string;
    name: string;
    content: string;
    fieldType: string;
    fieldValue: string;
    orderBy: number;
  };
}
