const { BASE_URL } = process.env;
export const account = {
  login: BASE_URL + '/account/login',
  logout: BASE_URL + '/account/logout',
};
export const index = {
  menu: BASE_URL + '/index/menu',
};
export const communityResident = BASE_URL + '/resident';
export const communityResidentBatch = BASE_URL + '/resident/batch';
export const dormitoryManager = BASE_URL + '/dormitory';
export const dormitoryManagerBatch = BASE_URL + '/dormitory/batch';
export const dormitoryManagerDownloadExcel = BASE_URL + '/dormitory/download';
export const company = BASE_URL + '/company';
export const companyBatch = BASE_URL + '/company/batch';
export const companySelect = BASE_URL + '/company/select-list';
export const system = {
  configuration: BASE_URL + '/system/configuration',
  configurationBatch: BASE_URL + '/system/configuration/batch',
  user: BASE_URL + '/system/user',
  userBatch: BASE_URL + '/system/user/batch',
  userSelectList: BASE_URL + '/system/user/select-list',
  permission: BASE_URL + '/system/permission',
  permissionBatch: BASE_URL + '/system/permission/batch',
  permissionSelectList: BASE_URL + '/system/permission/select-list',
};
