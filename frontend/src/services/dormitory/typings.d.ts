declare module '*.less';
declare namespace API {
  import type {
    EducationStatusEnum,
    EmploymentStatusEnum,
    GenderEnum,
    PoliticalStatusEnum,
  } from '@/services/enums';
  type DormitoryManager = {
    id?: number;
    name?: string;
    gender?: GenderEnum;
    birth?: Date;
    politicalStatus?: PoliticalStatusEnum;
    employmentStatus?: EmploymentStatusEnum;
    education?: EducationStatusEnum;
    address?: string;
    managerAddress?: string;
    managerCount?: number;
    systemUserId?: number;
    companyId?: number;
    phoneNumbers?: PhoneNumber[];
    systemUserInfo?: string[];
  };
}
