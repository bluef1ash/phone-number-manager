declare module '*.less';
declare namespace API {
  type CommunityResident = BaseEntity & {
    name?: string;
    address?: string;
    systemUserId?: number;
    companyId?: number;
    phoneNumbers?: PhoneNumber[];
    subcontractorInfo?: string[];
  };
}
