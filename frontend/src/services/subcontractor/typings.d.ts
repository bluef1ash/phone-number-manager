declare module '*.less';
declare namespace API {
  type Subcontractor = BaseEntity & {
    name: string;
    idCardNumber: string;
    positions?: string[] | { position: string }[];
    titles?: string[] | { title: string }[];
    companyId: number;
    phoneNumbers?: PhoneNumber[];
    company?: Company;
  };
}
