package com.itisacat.com.common.core.util;

import com.itisacat.basic.framework.core.util.EmptyUtils;
import com.itisacat.com.common.core.model.ColumeNameEntity;
import com.itisacat.com.common.core.model.ExcelColumnHeader;
import com.itisacat.com.common.core.model.ExcelColumnMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
public class ExcelUtil {

    /**
     * 导出excel
     */
    @SuppressWarnings("rawtypes")
    public static void saveExcel(String filePath, List objs, Class clazz, String sheetName, int pageSize, List<ColumeNameEntity> columnNames) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath);
            HSSFWorkbook workbook = handleDataToExcel(objs, clazz, sheetName, pageSize, columnNames);
            workbook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("exportToExcel, error=", e);
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 导出excel
     */
    @SuppressWarnings("rawtypes")
    public static void exportToExcel(HttpServletResponse response, String fileName, List objs, Class clazz,
                                     String sheetName, int pageSize, List<ColumeNameEntity> columnNames) {
        OutputStream out = null;
        try {
            String tempName = new String(fileName.getBytes(), "ISO8859-1");
            response.setHeader("content-disposition", "attachment;filename=" + tempName + ".xls");
            response.setContentType("application/ms-excel");
            HSSFWorkbook workbook = handleDataToExcel(objs, clazz, sheetName, pageSize, columnNames);
            out = response.getOutputStream();
            workbook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("exportToExcel, error=", e);
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public static HSSFWorkbook handleDataToExcel(List list, Class clazz, String sheetName, int pageSize, List<ColumeNameEntity> columnNames) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
        if (list != null && list.size() > 0) {
            List<ExcelColumnHeader> headers = getHeaderList(clazz, columnNames);
            Collections.sort(headers);
            int sheetCount = list.size() % pageSize == 0 ? list.size() / pageSize : list.size() / pageSize + 1;
            for (int i = 1; i <= sheetCount; i++) {
                Sheet sheet;
                if (!StringUtils.isEmpty(sheetName)) {
                    sheet = workbook.createSheet(sheetName + i);
                } else {
                    sheet = workbook.createSheet();
                }
                Row row = sheet.createRow(0);
                CellStyle titleStyle = setCellStyle(workbook, "title");  // 写标题
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellStyle(titleStyle);
                    cell.setCellValue(headers.get(j).getTitle());
                    sheet.setColumnWidth(j, headers.get(j).getWidth() * 256);
                }
                CellStyle bodyStyle = setCellStyle(workbook, "body");     // 写内容
                int begin = (i - 1) * pageSize;
                int end = (begin + pageSize) > list.size() ? list.size() : (begin + pageSize);
                int rowCount = 1;
                for (int n = begin; n < end; n++) {
                    row = sheet.createRow(rowCount);
                    rowCount++;
                    Object obj = list.get(n);
                    for (int x = 0; x < headers.size(); x++) {
                        Cell cell = row.createCell(x);
                        cell.setCellStyle(bodyStyle);
                        Object value = null;
                        if (headers.get(x).getArg() != null) {
                            Method method = clazz.getDeclaredMethod(headers.get(x).getMethodName(), Long.class);
                            value = method.invoke(obj, headers.get(x).getArg());
                        } else {
                            Method method = clazz.getDeclaredMethod(headers.get(x).getMethodName());
                            value = method.invoke(obj);
                        }
                        if (value instanceof Date) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String formattedDate = dateFormat.format((Date) value);
                            cell.setCellValue(formattedDate);
                        } else if (value instanceof Double) {
                            cell.setCellValue((Double) value);
                        } else if (value instanceof String) {
                            cell.setCellValue((String) value);
                        } else if (value instanceof Integer) {
                            cell.setCellValue((Integer) value);
                        } else if (value instanceof Long) {
                            cell.setCellValue((Long) value);
                        } else if (value instanceof BigDecimal) {
                            Float temp = ((BigDecimal) value).floatValue();
                            if (temp != null) {
                                cell.setCellValue(temp + "");
                            }

                        } else if (value instanceof Float) {
                            Float temp = (Float) value;
                            if (temp != null) {
                                cell.setCellValue(temp + "");
                            }

                        }
                    }
                }
            }
        } else {
            Sheet sheet = workbook.createSheet(sheetName);
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue("暂无数据");
        }
        return workbook;
    }

    private static CellStyle setCellStyle(HSSFWorkbook workbook,
                                          String position) {
        CellStyle style = workbook.createCellStyle();
        // 设置单元格字体水平、垂直居中
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        // 设置单元格边框
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        // 设置单元格字体
        Font font = workbook.createFont();
        font.setFontName("宋体");
        if ("title".equals(position)) {
            font.setFontHeightInPoints((short) 11);
            font.setBold(true);
        } else {
            font.setFontHeightInPoints((short) 10);
        }
        style.setFont(font);
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        style.setWrapText(true);
        return style;
    }

    @SuppressWarnings("rawtypes")
    private static List<ExcelColumnHeader> getHeaderList(Class clazz, List<ColumeNameEntity> list) {
        List<ExcelColumnHeader> headers = new ArrayList<ExcelColumnHeader>();
        Method[] ms = clazz.getDeclaredMethods();
        for (Method m : ms) {
            String mn = m.getName();
            if (mn.startsWith("get")) {
                if (m.isAnnotationPresent(ExcelColumnMapper.class)) {
                    ExcelColumnMapper dataMapper = m.getAnnotation(ExcelColumnMapper.class);
                    if (dataMapper.dynamicColumn() && EmptyUtils.isNotEmpty(list)) {
                        for (int i = 0; i < list.size(); i++) {
                            headers.add(new ExcelColumnHeader(list.get(i).getTitle(), dataMapper.order() + 1.0d * i / list.size(), dataMapper.width(), mn, list.get(i).getValue()));
                        }
                    } else {
                        headers.add(new ExcelColumnHeader(dataMapper.title(), dataMapper.order(), dataMapper.width(), mn, null));
                    }

                }
            }
        }
        return headers;
    }

    /**
     * 判断excel版本
     *
     * @param in
     * @param filename
     * @return
     * @throws IOException
     */
    private static Workbook openWorkbook(InputStream in, String filename)
            throws IOException {
        Workbook wb;
        if (filename.endsWith(".xlsx")) {
            wb = new XSSFWorkbook(in);// Excel 2007
        } else {
            wb = new HSSFWorkbook(in);// Excel 2003
        }
        return wb;
    }

    /**
     * 文件导入
     *
     * @param in
     * @param fileName
     * @param sheetIndex
     * @return
     * @throws Exception
     */
    public static List<List<String>> getExcelData(InputStream in, String fileName, int sheetIndex) throws Exception {
        List<List<String>> dataList = new ArrayList<List<String>>();
        Workbook workbook = openWorkbook(in, fileName);
        Sheet sheet = workbook.getSheetAt(sheetIndex);// 切换工作薄
        Row row = null;
        Cell cell = null;
        int totalRows = sheet.getPhysicalNumberOfRows();
        /** 得到Excel的列数 */
        int totalCells = totalRows >= 1 && sheet.getRow(0) != null ? sheet
                .getRow(0).getPhysicalNumberOfCells() : 0;
        for (int r = 0; r < totalRows; r++) {
            row = sheet.getRow(r);
            if (row == null || curRowInsideNull(row, totalCells))
                continue;
            List<String> rowList = new ArrayList<String>();
            for (int c = 0; c < totalCells; c++) {
                cell = row.getCell(c);
                String cellValue = "";
                if (null != cell) {
                    // 以下是判断数据的类型
                    switch (cell.getCellType()) {
                        case HSSFCell.CELL_TYPE_NUMERIC: // 数字
                            int cellStyle = cell.getCellStyle().getDataFormat();
                            String cellStyleStr = cell.getCellStyle().getDataFormatString();
                            if ("0.00_);[Red]\\(0.00\\)".equals(cellStyleStr)) {
                                NumberFormat f = new DecimalFormat("#.##");
                                cellValue = (f.format((cell.getNumericCellValue())) + "")
                                        .trim();
                            } else if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                cellValue = HSSFDateUtil.getJavaDate(
                                        cell.getNumericCellValue()).toString();
                            } else if (cellStyle == 58 || cellStyle == 179 || "m\"月\"d\"日\";@".equals(cellStyleStr)) {
                                // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
                                SimpleDateFormat sdf = new SimpleDateFormat(
                                        "yyyy-MM-dd");
                                double value = cell.getNumericCellValue();
                                Date date = DateUtil
                                        .getJavaDate(value);
                                cellValue = sdf.format(date);
                            } else if ("[$-804]aaaa;@".equals(cellStyleStr)) {
                                SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                                double value = cell.getNumericCellValue();
                                Date date = DateUtil
                                        .getJavaDate(value);
                                cellValue = sdf.format(date);
                            } else {
                                NumberFormat f = new DecimalFormat("#.##");
                                cellValue = (f.format((cell.getNumericCellValue())) + "")
                                        .trim();
                            }
                            break;
                        case HSSFCell.CELL_TYPE_STRING: // 字符串
                            cellValue = cell.getStringCellValue();
                            break;
                        case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
                            cellValue = cell.getBooleanCellValue() + "";
                            break;
                        case HSSFCell.CELL_TYPE_FORMULA: // 公式
                            try {
                                cellValue = String.valueOf(cell.getNumericCellValue());
                            } catch (IllegalStateException e) {
                                log.info("exportToExcel, error=", e);
                                try {
                                    cellValue = String.valueOf(cell.getRichStringCellValue());
                                } catch (Exception e1) {
                                    cellValue = "";
                                }
                            }
                            break;
                        case HSSFCell.CELL_TYPE_BLANK: // 空值
                            break;
                        case HSSFCell.CELL_TYPE_ERROR: // 故障
                            break;
                        default:
                            break;
                    }
                }
                rowList.add(cellValue);
            }
            dataList.add(rowList);
        }
        return dataList;
    }

    /**
     * 判断当前行内所有单元格是否为空
     *
     * @param row
     * @param totalCells
     * @return
     */
    private static boolean curRowInsideNull(Row row, int totalCells) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < totalCells; i++) {
            row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell != null) {
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        sb.append(cell.getStringCellValue().trim());
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        sb.append(String.valueOf(cell.getNumericCellValue()));
                        break;
                    case Cell.CELL_TYPE_BOOLEAN:
                        sb.append(String.valueOf(cell.getBooleanCellValue()));
                        break;
                    case Cell.CELL_TYPE_FORMULA://判断公式生成的结果
                        sb.append(String.valueOf(cell.getNumericCellValue()));
                        break;
                    default:
                        break;
                }
            }
        }
        if (sb.toString().trim().equals(""))
            return true;
        return false;
    }
}
