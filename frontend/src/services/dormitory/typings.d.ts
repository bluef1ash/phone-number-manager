declare module '*.less';
declare namespace API {
  enum GenderEnum {
    MALE = 'MALE', FEMALE = 'FEMALE', UNKNOWN = 'UNKNOWN'
  }

  enum PoliticalStatusEnum {
    MALE = "MALE",
    PARTY_MEMBER = "PARTY_MEMBER",
    PREPARATORY_COMMUNISTS = "PREPARATORY_COMMUNISTS",
    COMMUNIST_YOUTH_LEAGUE_MEMBER = "COMMUNIST_YOUTH_LEAGUE_MEMBER",
    PREPARING_COMMUNIST_YOUTH_LEAGUE_MEMBER = "PREPARING_COMMUNIST_YOUTH_LEAGUE_MEMBER",
    OTHER = "OTHER"
  }

  enum EmploymentStatusEnum {
    WORK = "WORK", RETIREMENT = "RETIREMENT", UNEMPLOYED = "UNEMPLOYED"
  }

  enum EducationStatusEnum {
    ILLITERACY = "ILLITERACY",
    PRIMARY_SCHOOL = "PRIMARY_SCHOOL",
    JUNIOR_HIGH_SCHOOL = "JUNIOR_HIGH_SCHOOL",
    TECHNICAL_SECONDARY_SCHOOL = "TECHNICAL_SECONDARY_SCHOOL",
    SENIOR_MIDDLE_SCHOOL = "SENIOR_MIDDLE_SCHOOL",
    JUNIOR_COLLEGE = "JUNIOR_COLLEGE",
    UNDERGRADUATE_COURSE = "UNDERGRADUATE_COURSE",
    MASTER = "MASTER",
    DOCTOR = "DOCTOR"
  }

  type DormitoryManager = {
    id: number;
    name: string;
    gender: GenderEnum;
    birth: Date;
    politicalStatus: PoliticalStatusEnum;
    employmentStatus: EmploymentStatusEnum;
    education: EducationStatusEnum;
    address: string;
    managerAddress: string;
    managerCount: number;
    systemUserId: number;
    companyId: number;
    phoneNumbers: PhoneNumber[];
  };
}
