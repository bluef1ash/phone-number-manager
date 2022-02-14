export const baseUrl = 'http://127.0.0.1:8080';
export const account = {
  login: baseUrl + '/account/login',
  logout: baseUrl + '/account/logout',
};
export const index = {
  menu: baseUrl + '/index/menu',
};
export const communityResident = baseUrl + '/resident';
export const communityResidentBatch = baseUrl + '/resident/batch';
export const dormitoryManager = baseUrl + '/dormitory';
export const dormitoryManagerBatch = baseUrl + '/dormitory/batch';
export const company = baseUrl + '/company';
export const companyBatch = baseUrl + '/company/batch';
export const companySelect = baseUrl + '/company/select-list';
export const system = {
  configuration: baseUrl + '/system/configuration',
  configurationBatch: baseUrl + '/system/configuration/batch',
  user: baseUrl + '/system/user',
  userBatch: baseUrl + '/system/user/batch',
  userSelectList: baseUrl + '/system/user/select-list',
  permission: baseUrl + '/system/permission',
  permissionBatch: baseUrl + '/system/permission/batch',
  permissionSelectList: baseUrl + '/system/permission/select-list',
};
