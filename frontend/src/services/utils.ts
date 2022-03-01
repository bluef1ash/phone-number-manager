import { parsePhoneNumber } from "libphonenumber-js/max";
import { PhoneNumberType } from "@/services/enums";

/**
 * 提交前联系方式处理
 * @param number 联系方式
 */
export function submitPrePhoneNumberHandle(number: string): API.PhoneNumber {
  const phoneType = parsePhoneNumber(number, 'CN').getType();
  let pt = PhoneNumberType.UNKNOWN;
  if (typeof phoneType !== 'undefined') {
    if (phoneType.toString() === 'FIXED_LINE') {
      pt = PhoneNumberType.FIXED_LINE;
    } else if (phoneType.toString() === 'MOBILE') {
      pt = PhoneNumberType.MOBILE;
    }
  }
  return {
    phoneType: pt,
    phoneNumber: number,
  };
}
