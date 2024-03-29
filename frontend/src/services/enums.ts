export enum ExceptionCode {
  NOT_LOGGED = 10000,
  ADD_FAILED,
  EDIT_FAILED,
  DELETE_FAILED,
  NOT_MODIFIED,
  METHOD_ARGUMENT_NOT_VALID,
  UNKNOWN_EXCEPTION,
  REPEAT_DATA_FAILED,
  FORBIDDEN,
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
  GET,
  HEAD,
  POST,
  PUT,
  PATCH,
  DELETE,
  OPTIONS,
  TRACE,
}

export enum MenuTypeEnum {
  ALL,
  FRONTEND,
  BACKEND,
}

export enum FieldTypeEnum {
  UNKNOWN,
  BOOLEAN,
  STRING,
  NUMBER,
  SYSTEM_USER,
}

export enum ImportOrExportStatusEnum {
  START,
  HANDLING,
  HANDLED,
  DOWNLOAD,
  UPLOAD,
  DONE,
  FAILED,
}
