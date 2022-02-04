declare module '*.less';
declare namespace API {
  type SystemUser = {
    id?: number;
    username?: string;
    position?: string[];
    title?: string[];
    isSubcontract?: boolean;
    loginTime?: Date;
    loginIp?: string;
    isLocked?: boolean;
    isEnabled?: boolean;
    accountExpireTime?: Date;
    credentialExpireTime?: Date;
    phoneNumberId?: number;
    companies?: Company[];
    phoneNumber?: string;
  };
}
