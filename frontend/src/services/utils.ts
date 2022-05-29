import { parsePhoneNumber } from "libphonenumber-js/max";
import { PhoneNumberType } from "@/services/enums";
import { message } from "antd";
import type { Dispatch, RefObject, SetStateAction } from "react";
import { SESSION_SYSTEM_USER_KEY } from "@config/constant";

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

/**
 * 获取单位父级编号数组
 * @return
 */
export function getCompanyParentIds(): number[] {
  let parentIds = [0];
  const systemUser: API.SystemUser = JSON.parse(
    localStorage.getItem(SESSION_SYSTEM_USER_KEY) as string,
  );
  if (
    typeof systemUser.companies !== 'undefined' &&
    systemUser.companies !== null &&
    systemUser.companies.length > 0
  ) {
    parentIds = systemUser.companies.map((company) => company.parentId as number);
  }
  return parentIds;
}

/**
 * 检测组件是否视口可见
 */
export function checkVisibleInDocument(componentRef: RefObject<HTMLElement>): boolean {
  if (
    componentRef.current === null ||
    !(
      componentRef.current.offsetHeight ||
      componentRef.current.offsetWidth ||
      componentRef.current.getClientRects().length
    )
  ) {
    return false;
  }
  const { height, top } = componentRef.current.getBoundingClientRect();
  const windowHeight = window.innerHeight || document.documentElement.clientHeight;
  return top < windowHeight && top + height > 0;
}
