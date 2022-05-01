declare module '*.less';
declare namespace API {
  type LoginResult = {
    code: number;
    token: string;
    systemUserInfo?: SystemUser;
  };

  type LoginParams = {
    username: string;
    password: string;
    autoLogin?: boolean;
    type?: string;
  };
}
