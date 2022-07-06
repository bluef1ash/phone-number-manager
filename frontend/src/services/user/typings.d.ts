declare module '*.less';
declare namespace API {
  type SystemUser = BaseEntity & {
    username?: string;
    loginTime?: Date;
    loginIp?: string;
    isLocked?: boolean;
    isEnabled?: boolean;
    accountExpireTime?: Date;
    credentialExpireTime?: Date;
    companies?: Company[];
    companyIds?: number[][];
  };
}
