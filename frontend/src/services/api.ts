export const baseUrl = 'http://127.0.0.1:8080';
export const account = {
  login: baseUrl + '/account/login',
  logout: baseUrl + '/account/logout',
};
export const index = {
  menu: baseUrl + '/index/menu',
};
export const communityResident = baseUrl + '/resident';
export const company = baseUrl + '/company';
export const companyBatch = baseUrl + '/company/batch';
export const system = {
  configuration: baseUrl + '/system/configuration',
  configurationBatch: baseUrl + '/system/configuration/batch',
  user: baseUrl + '/system/user',
  userBatch: baseUrl + '/system/user/batch',
  permission: baseUrl + '/system/permission',
  permissionBatch: baseUrl + '/system/permission/batch',
  permissionSelectList: baseUrl + '/system/permission/select-list',
};
