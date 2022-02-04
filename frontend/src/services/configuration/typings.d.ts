declare module '*.less';
declare namespace API {
  type Configuration = {
    createTime: Date;
    updateTime: Date;
    version: number;
    id: number;
    title: string;
    description: string;
    name: string;
    content: string;
    fieldType: string;
    fieldValue: string;
    orderBy: number;
  };
}
