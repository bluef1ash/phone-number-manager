package com.github.phonenumbermanager.handler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.poi.ss.usermodel.*;

import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.github.phonenumbermanager.dto.CommunityResidentExcelDTO;
import com.github.phonenumbermanager.util.CommonUtil;

import cn.hutool.poi.excel.RowUtil;
import cn.hutool.poi.excel.cell.CellUtil;

/**
 * 社区居民复杂表标题写入处理器
 *
 * @author 廿二月的天
 */
public class CommunityResidentTitleSheetWriteHandler extends TitleSheetWriteHandler {

    public CommunityResidentTitleSheetWriteHandler(String excelTitleUp, String excelTitle) {
        this.excelTitleUp = excelTitleUp;
        this.excelTitle = excelTitle;
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Workbook workbook = writeWorkbookHolder.getWorkbook();
        Sheet sheet = writeSheetHolder.getSheet();
        titleUpHandler(workbook, sheet);
        CommonUtil.exportExcelTitleHandle(workbook, sheet, excelTitle,
            CommunityResidentExcelDTO.class.getDeclaredFields().length - 1);
        Row dateRow = RowUtil.getOrCreateRow(sheet, 2);
        Cell dateRowCell = CellUtil.getOrCreateCell(dateRow, 6);
        dateRowCell.setCellValue("时间：" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")));
        CellStyle dateRowStyle =
            CommonUtil.cellStyleHandle(dateRowCell, workbook, "宋体", (short)11, false, false, false);
        CellUtil.mergingCells(sheet, 2, 2, 6, 7, dateRowStyle);
    }
}
