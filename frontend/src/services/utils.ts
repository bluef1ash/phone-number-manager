import { parsePhoneNumber } from "libphonenumber-js/max";
import { PhoneNumberType } from "@/services/enums";
import { message } from "antd";
import type { Dispatch, SetStateAction } from "react";

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

/**
 * 下载Excel文件
 * @param setSpinState
 * @param setSpinTipState
 * @param downloadUrl
 * @param spinTip
 * @param filename
 */
export async function downloadExcelFile(
  setSpinState: Dispatch<SetStateAction<boolean>>,
  setSpinTipState: Dispatch<SetStateAction<string>>,
  downloadUrl: Promise<any>,
  spinTip: string[],
  filename?: string,
) {
  setSpinTipState(spinTip[0]);
  setSpinState(true);
  const { data, response } = await downloadUrl;
  if (typeof data.code === 'undefined' && data) {
    let customFilename = response.headers.get('Content-Disposition');
    if (customFilename !== null) {
      customFilename = decodeURI(customFilename.substring('attachment;filename='.length));
    } else {
      customFilename = filename;
    }
    const blob = await data;
    setSpinTipState(spinTip[1]);
    const link = document.createElement('a');
    if ('download' in link) {
      link.style.display = 'none';
      link.href = URL.createObjectURL(blob);
      link.download = customFilename;
      document.body.appendChild(link);
      link.click();
      URL.revokeObjectURL(link.href);
      document.body.removeChild(link);
    } else {
      //@ts-ignore
      navigator.msSaveBlob(blob, customFilename);
    }
  } else {
    message.error('导出失败，请稍后再试！');
  }
  setSpinState(false);
}
