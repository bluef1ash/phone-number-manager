declare module '*.less';
declare namespace API {
  type LoginResult = {
    code: number;
    token: string;
    currentUser?: SystemUser;
  };

  type LoginParams = {
    username: string;
    password: string;
    autoLogin?: boolean;
    type?: string;
  };
}
