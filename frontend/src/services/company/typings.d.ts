declare module '*.less';
declare namespace API {
  type Company = {
    id: number;
    name: string;
    actualNumber?: number;
    level?: number;
    phoneNumbers?: PhoneNumber[];
    parentId?: number;
    children?: Company[];
  };
}
