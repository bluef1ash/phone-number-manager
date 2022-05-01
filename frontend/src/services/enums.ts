export enum ExceptionCode {
  NOT_LOGGED = 10000,
  ADD_FAILED,
  EDIT_FAILED,
  DELETE_FAILED,
  NOT_MODIFIED,
  METHOD_ARGUMENT_NOT_VALID,
  UNKNOWN_EXCEPTION,
  REPEAT_DATA_FAILED,
}

export enum PhoneNumberType {
  UNKNOWN,
  MOBILE,
  FIXED_LINE,
}

export enum GenderEnum {
  MALE,
  FEMALE,
  UNKNOWN,
}

export enum PoliticalStatusEnum {
  MALE,
  PARTY_MEMBER,
  PREPARATORY_COMMUNISTS,
  COMMUNIST_YOUTH_LEAGUE_MEMBER,
  PREPARING_COMMUNIST_YOUTH_LEAGUE_MEMBER,
  OTHER,
}

export enum EmploymentStatusEnum {
  WORK,
  RETIREMENT,
  UNEMPLOYED,
}

export enum EducationStatusEnum {
  ILLITERACY,
  PRIMARY_SCHOOL,
  JUNIOR_HIGH_SCHOOL,
  TECHNICAL_SECONDARY_SCHOOL,
  SENIOR_MIDDLE_SCHOOL,
  JUNIOR_COLLEGE,
  UNDERGRADUATE_COURSE,
  MASTER,
  DOCTOR,
}

export enum HttpMethod {
  GET = 'GET',
  HEAD = 'HEAD',
  POST = 'POST',
  PUT = 'PUT',
  PATCH = 'PATCH',
  DELETE = 'DELETE',
  OPTIONS = 'OPTIONS',
  TRACE = 'TRACE',
}

export enum MenuTypeEnum {
  ALL = 'ALL',
  FRONTEND = 'FRONTEND',
  BACKEND = 'BACKEND',
}

export enum ComputedDataTypes {
  /**
   * 社区居民基本信息
   */
  RESIDENT_BASE_MESSAGE = 1,
  /**
   * 社区居民柱状图表
   */
  RESIDENT_BAR_CHART,
  /**
   * 社区楼长基本信息
   */
  DORMITORY_BASE_MESSAGE,
  /**
   * 社区楼长柱状图表
   */
  DORMITORY_BAR_CHART,
  /**
   * 社区居民分包人柱状图表
   */
  RESIDENT_SUBCONTRACTOR_BAR_CHART,
}
