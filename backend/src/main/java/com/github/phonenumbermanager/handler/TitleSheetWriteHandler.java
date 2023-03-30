package com.github.phonenumbermanager.handler;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.github.phonenumbermanager.util.CommonUtil;

import cn.hutool.poi.excel.RowUtil;
import cn.hutool.poi.excel.cell.CellUtil;

/**
 * 复杂表标题写入处理器
 *
 * @author 廿二月的天
 */
public abstract class TitleSheetWriteHandler implements SheetWriteHandler {
    protected String excelTitleUp;
    protected String excelTitle;

    /**
     * 标题上面小标题处理
     *
     * @param workbook
     *            工作簿对象
     * @param sheet
     *            工作表对象
     */
    protected void titleUpHandler(Workbook workbook, Sheet sheet) {
        Row firstRow = RowUtil.getOrCreateRow(sheet, 0);
        Cell firstRowCell = CellUtil.getOrCreateCell(firstRow, 0);
        firstRowCell.setCellValue(excelTitleUp);
        CommonUtil.cellStyleHandle(firstRowCell, workbook, "宋体", (short)12, true, false, false);
    }
}
