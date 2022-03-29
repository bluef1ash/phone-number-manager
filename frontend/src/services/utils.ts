import { parsePhoneNumber } from "libphonenumber-js/max";
import { PhoneNumberType } from "@/services/enums";
import { downloadCommunityResidentExcel } from "@/services/resident/api";
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
 */
export async function downloadExcelFile(
  setSpinState: Dispatch<SetStateAction<boolean>>,
  setSpinTipState: Dispatch<SetStateAction<string>>,
) {
  setSpinTipState('正在生成社区居民花名册中...');
  setSpinState(true);
  const { data, response } = await downloadCommunityResidentExcel();
  if (typeof data.code === 'undefined' && data) {
    let filename = response.headers.get('Content-Disposition');
    if (filename !== null) {
      filename = decodeURI(filename.substring('attachment;filename='.length));
    } else {
      filename = '“评社区”活动电话库登记表.xlsx';
    }
    const blob = await data;
    setSpinTipState('正在下载社区居民花名册中...');
    const link = document.createElement('a');
    if ('download' in link) {
      link.style.display = 'none';
      link.href = URL.createObjectURL(blob);
      link.download = filename;
      document.body.appendChild(link);
      link.click();
      URL.revokeObjectURL(link.href);
      document.body.removeChild(link);
    } else {
      //@ts-ignore
      navigator.msSaveBlob(blob, filename);
    }
  } else {
    message.error('导出失败，请稍后再试！');
  }
  setSpinState(false);
}
