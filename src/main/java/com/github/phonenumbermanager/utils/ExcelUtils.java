package com.github.phonenumbermanager.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.poi.ss.usermodel.CellType.NUMERIC;

/**
 * Excel工具类
 *
 * @author 廿二月的天
 */
public class ExcelUtils {
    /**
     * 2003- 版本的excel
     */
    private final static String EXCEL2003L = ".xls";
    /**
     * 2007+ 版本的excel
     */
    private final static String EXCEL2007U = ".xlsx";
    /**
     * 未定义的字段
     */
    public static final String NO_DEFINE = "no_define";
    /**
     * 默认格式
     */
    private static final String DEFAULT_PATTERN = "General";
    /**
     * 默认单元格宽度
     */
    private static final int DEFAULT_COLUMN_WIDTH = 17;
    /**
     * 默认英文日期格式
     */
    public static final String DEFAULT_DATE_PATTERN_EN = "m/d/yy";
    /**
     * 作者信息
     */
    private static final String AUTHOR = "廿二月的天";

    /**
     * 数据单元格样式对象
     */
    private static CellStyle contentStyle;

    /**
     * 参数
     */
    private static String[] properties;

    /**
     * 标题数组
     */
    private static String[] headers;
    /**
     * 回调接口对象
     */
    private static DataHandler dataHandler;

    /**
     * 导出Excel 97(.xls)格式 ，少量数据
     *
     * @param company   单位
     * @param title     标题行
     * @param sheetName 工作表名称
     * @param headMap   属性-列名
     * @param jsonArray 数据集
     * @param colWidth  列宽 默认 至少17个字节
     * @param handler   表头处理函数
     * @return 数据流
     * @throws IOException 输入、输出流异常
     */
    @Deprecated
    public static ByteArrayOutputStream exportExcel(String company, String title, String sheetName, Map<String, String> headMap, JSONArray jsonArray, int colWidth, DataHandler handler) throws IOException {
        dataHandler = handler;
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        workbook.createInformationProperties();
        workbook.getDocumentSummaryInformation().setCompany(company);
        SummaryInformation si = workbook.getSummaryInformation();
        // 添加xls文件作者信息
        si.setAuthor(AUTHOR);
        // 添加xls文件创建程序信息
        si.setApplicationName("导出程序");
        //填加xls文件最后保存者信息
        si.setLastAuthor(AUTHOR);
        //填加xls文件作者信息
        si.setComments("此程序由系统生成");
        //填加xls文件标题信息
        si.setTitle(title);
        //填加文件主题信息
        si.setSubject(title);
        si.setCreateDateTime(new Date());
        // 生成一个(带标题)表格
        HSSFSheet sheet = workbook.createSheet(sheetName);
        //设置列宽至少字节数
        setColumnWidth(headMap, colWidth, sheet);
        // 遍历集合数据，产生数据行
        setDataColumn(workbook, sheet, false, headMap, jsonArray, colWidth);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        workbook.close();
        return byteArrayOutputStream;
    }

    /**
     * 导出Excel 2007 OOXML (.xlsx)格式
     *
     * @param sheetName 工作表名称
     * @param headMap   表头
     * @param data      数据集
     * @param colWidth  列宽 默认 至少17个字节
     * @param handler   表头处理函数
     * @return 数据流
     * @throws IOException IO流异常
     */
    public static ByteArrayOutputStream exportExcelX(String sheetName, Map<String, String> headMap, JSONArray data, int colWidth, DataHandler handler) throws IOException {
        dataHandler = handler;
        // 声明一个工作薄
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        // 文件信息

        // 缓存
        workbook.setCompressTempFiles(true);
        // 生成一个(带标题)表格
        SXSSFSheet sheet = workbook.createSheet(sheetName);
        // 设置列宽
        setColumnWidth(headMap, colWidth, sheet);
        // 遍历集合数据，产生数据行
        setDataColumn(workbook, sheet, true, headMap, data, colWidth);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        workbook.close();
        workbook.dispose();
        return byteArrayOutputStream;
    }

    /**
     * 导出下载excel
     *
     * @param response              HTTP响应对象
     * @param request               HTTP请求对象
     * @param title                 文件标题
     * @param byteArrayOutputStream 数据流
     * @throws Exception 文件流异常/字符串转换异常
     */
    public static void downloadExcelFile(HttpServletResponse response, HttpServletRequest request, String title, ByteArrayOutputStream byteArrayOutputStream) throws Exception {
        byte[] content = byteArrayOutputStream.toByteArray();
        InputStream is = new ByteArrayInputStream(content);
        // 设置response参数，可以打开下载页面
        response.reset();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + new String((title + ".xlsx").getBytes(), StandardCharsets.ISO_8859_1));
        response.setContentLength(content.length);
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if ("exportExcel".equals(cookie.getName())) {
                cookie.setValue("success");
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        }
        ServletOutputStream outputStream = response.getOutputStream();
        BufferedInputStream bis = new BufferedInputStream(is);
        BufferedOutputStream bos = new BufferedOutputStream(outputStream);
        byte[] buff = new byte[8192];
        int bytesRead;
        while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
            bos.write(buff, 0, bytesRead);
        }
        bis.close();
        bos.close();
        outputStream.flush();
        outputStream.close();
    }

    /**
     * 描述：对表格中数值进行格式化
     *
     * @param cell     单元格对象
     * @param cellType 单元格类型
     * @return 单元格数据
     */
    public static Object getCellValue(Cell cell, CellType cellType) {
        Object value = null;
        if (cell != null) {
            if (cellType == null) {
                cellType = cell.getCellTypeEnum();
            }
            switch (cellType) {
                case STRING:
                    DataFormatter dataFormatter = new DataFormatter();
                    value = dataFormatter.formatCellValue(cell);
                    break;
                case NUMERIC:
                    //格式化number String字符
                    DecimalFormat df = new DecimalFormat("0");
                    //日期格式化
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    //格式化数字
                    DecimalFormat df2 = new DecimalFormat("0.00");
                    cell.setCellType(NUMERIC);
                    if (DEFAULT_PATTERN.equals(cell.getCellStyle().getDataFormatString())) {
                        value = df.format(cell.getNumericCellValue());
                    } else if (DEFAULT_DATE_PATTERN_EN.equals(cell.getCellStyle().getDataFormatString())) {
                        value = sdf.format(cell.getDateCellValue());
                    } else {
                        value = df2.format(cell.getNumericCellValue());
                    }
                    break;
                case BOOLEAN:
                    value = cell.getBooleanCellValue();
                    break;
                case BLANK:
                    value = "";
                    break;
                default:
                    break;
            }
        }
        return value;
    }

    /**
     * 描述：获取IO流中的数据，组装成List<List<Object>>对象
     *
     * @param in       输入流对象
     * @param fileName 文件名称
     * @return 文件内容
     * @throws Exception 操作异常
     */
    public List<List<Object>> getBankListByExcel(InputStream in, String fileName) throws Exception {
        //创建Excel工作薄
        Workbook work = getWorkbook(in, fileName);
        Sheet sheet;
        Row row;
        Cell cell;
        List<List<Object>> list = new ArrayList<>();
        //遍历Excel中所有的sheet
        for (int i = 0; i < work.getNumberOfSheets(); i++) {
            sheet = work.getSheetAt(i);
            if (sheet == null) {
                continue;
            }
            //遍历当前sheet中的所有行
            for (int j = sheet.getFirstRowNum(); j < sheet.getLastRowNum(); j++) {
                row = sheet.getRow(j);
                if (row == null || row.getFirstCellNum() == j) {
                    continue;
                }
                //遍历所有的列
                List<Object> li = new ArrayList<>();
                for (int y = row.getFirstCellNum(); y < row.getLastCellNum(); y++) {
                    cell = row.getCell(y);
                    li.add(getCellValue(cell, null));
                }
                list.add(li);
            }
        }
        work.close();
        return list;
    }

    /**
     * 描述：根据文件后缀，自适应上传文件的版本
     *
     * @param inputStream 输入流对象
     * @param fileName    文件名
     * @return 文件对象
     * @throws Exception 文件后缀解析异常
     */
    public static Workbook getWorkbook(InputStream inputStream, String fileName) throws Exception {
        Workbook wb;
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        if (EXCEL2003L.equals(fileType)) {
            //2003-
            wb = new HSSFWorkbook(inputStream);
        } else if (EXCEL2007U.equals(fileType)) {
            //2007+
            wb = new XSSFWorkbook(inputStream);
        } else {
            throw new Exception("解析的文件格式有误！");
        }
        return wb;
    }

    /**
     * 根据列索引获取列名称
     *
     * @param columnIndex 列索引
     * @return 列名称
     */
    public static String getExcelColumnLabel(int columnIndex) {
        String columnString;
        int baseCol = 65 + columnIndex;
        if (baseCol > 90) {
            int i2;
            if ((baseCol - 90) / 26 > 0) {
                i2 = 65 + (baseCol - 90 - 1) / 26;
            } else {
                i2 = 65;
            }
            int i1 = (baseCol - 90 - 1) % 26;
            i1 = 65 + i1;
            columnString = String.valueOf((char) i2) + (char) i1;
        } else {
            columnString = String.valueOf((char) baseCol);
        }
        return columnString;
    }

    /**
     * 判断指定的单元格是否是合并单元格
     *
     * @param sheet  工作表
     * @param row    行下标
     * @param column 列下标
     * @return 是否已经合并
     */
    public static boolean isMergedRegion(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 设置单元格样式
     *
     * @param workbook   工作簿对象
     * @param fontName   字体名称
     * @param fontHeight 字体大小
     * @param isBold     是否加粗
     * @param isBorder   是否有边框
     * @param isWrapText 是否自动换行
     */
    public static CellStyle setCellStyle(Workbook workbook, String fontName, short fontHeight, boolean isBold, boolean isBorder, boolean isWrapText) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillPattern(FillPatternType.NO_FILL);
        if (isBorder) {
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
        }
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setFontName(fontName);
        font.setFontHeightInPoints(fontHeight);
        font.setBold(isBold);
        cellStyle.setFont(font);
        cellStyle.setWrapText(isWrapText);
        return cellStyle;
    }

    /**
     * 设置数据
     *
     * @param workbook 工作簿对象
     * @param sheet    工作表对象
     * @param isExcelX 是否为2007以上版本
     * @param headMap  表头
     * @param data     数据集
     * @param colWidth 列宽 默认 至少17个字节
     */
    private static void setDataColumn(Workbook workbook, Sheet sheet, boolean isExcelX, Map<String, String> headMap, JSONArray data, int colWidth) {
        int rowIndex = 0;
        Map<String, Object> params = new HashMap<>(3);
        for (Object obj : data) {
            // 标题和表头
            if (rowIndex == 65535 || rowIndex == 0) {
                if (rowIndex != 0) {
                    //如果数据超过了，则在第二页显示
                    sheet = workbook.createSheet();
                    setColumnWidth(headMap, colWidth, sheet);
                }
                params.put("workbook", workbook);
                params.put("sheet", sheet);
                params.put("rowIndex", rowIndex);
                params.put("contentStyle", contentStyle);
                dataHandler.headHandler(params, headers);
                workbook = (Workbook) params.get("workbook");
                sheet = (Sheet) params.get("sheet");
                rowIndex = (int) params.get("rowIndex");
                contentStyle = (CellStyle) params.get("contentStyle");
            }
            // 数据内容
            JSONObject jsonObject = (JSONObject) JSONObject.toJSON(obj);
            Row dataRow = sheet.createRow(rowIndex);
            for (int i = 0; i < properties.length; i++) {
                Cell newCell = dataRow.createCell(i);
                Object object = jsonObject.get(properties[i]);
                if (object == null) {
                    newCell.setCellType(CellType.STRING);
                    newCell.setCellValue("");
                } else if (object instanceof Date) {
                    newCell.setCellType(NUMERIC);
                    newCell.setCellValue((Date) object);
                } else if (object instanceof Double) {
                    newCell.setCellType(CellType.NUMERIC);
                    newCell.setCellValue((Double) object);
                } else if (object instanceof Float) {
                    newCell.setCellType(CellType.NUMERIC);
                    newCell.setCellValue((Float) object);
                } else if (object instanceof Integer) {
                    newCell.setCellType(CellType.NUMERIC);
                    newCell.setCellValue((Integer) object);
                } else if (object instanceof Long) {
                    newCell.setCellType(CellType.NUMERIC);
                    newCell.setCellValue((Long) object);
                } else {
                    newCell.setCellType(CellType.STRING);
                    newCell.setCellValue(object.toString());
                }
                newCell.setCellStyle(contentStyle);
            }
            rowIndex++;
        }
        if (isExcelX) {
            ((SXSSFSheet) sheet).trackAllColumnsForAutoSizing();
        }
        // 自动调整宽度
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, headers[i].getBytes().length * 2 * 256);
        }
    }

    /**
     * 设置列宽
     *
     * @param headMap  标题数据
     * @param colWidth 列宽
     * @param sheet    工作表
     */
    private static void setColumnWidth(Map<String, String> headMap, int colWidth, Sheet sheet) {
        // 至少字节数
        int[] arrColWidth = new int[headMap.size()];
        // 产生表格标题行,以及设置列宽
        properties = new String[headMap.size()];
        headers = new String[headMap.size()];
        int minBytes = Math.max(colWidth, DEFAULT_COLUMN_WIDTH);
        int index = 0;
        for (Map.Entry<String, String> entry : headMap.entrySet()) {
            properties[index] = entry.getKey();
            headers[index] = entry.getValue();
            int bytes = entry.getValue().getBytes().length;
            arrColWidth[index] = Math.max(bytes, minBytes);
            sheet.setColumnWidth(index, arrColWidth[index] * 256);
            index++;
        }
    }

    /**
     * 回调接口
     */
    public interface DataHandler {
        /**
         * 处理标题与表头
         *
         * @param params  传参集合
         * @param headers 表头数组
         */
        void headHandler(Map<String, Object> params, String[] headers);
    }
}
