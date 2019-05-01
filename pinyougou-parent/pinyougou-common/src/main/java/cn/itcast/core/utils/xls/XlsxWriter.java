package cn.itcast.core.utils.xls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Max
 * @date 2016/10/13
 */
public class XlsxWriter {

    /**
     * 导出多sheet的excel
     * @param sheets key作为sheetName
     * @param fieldNames 指定T.class中哪些属性需要导出
     */
    public static <T> void writeMultiSheet(Map<String, List<T>> sheets, String[] fieldNames, OutputStream out) throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook();
        for (Map.Entry<String, List<T>> sheetData : sheets.entrySet()) {
            String sheetName = sheetData.getKey();
            List<T> data = sheetData.getValue();
            XSSFSheet sheet = wb.createSheet(sheetName);
            if (data == null || data.size() == 0){
                continue;
            }
            XSSFRow row0 = sheet.createRow(0);
            T first = data.get(0);
            int fieldnum = 0;
            for (String fieldName : fieldNames){
                Field f = FieldUtils.getField(first.getClass(), fieldName, true);
                if (!f.isAnnotationPresent(ExcelField.class)) {
                    throw new IllegalArgumentException("field " + fieldName + " can not found an annotation named ExcelField.class");
                }
                ExcelField annotation = f.getAnnotation(ExcelField.class);
                String title = annotation.title();
                if ("".equals(title)){
                    title = f.getName();
                }
                XSSFCell cell = row0.createCell(fieldnum++);
                cell.setCellStyle(createHeaderStyle(wb, f));
                cell.setCellValue(title);
            }
            for (int rownum = 0; rownum < data.size(); rownum++) {
                T t = data.get(rownum);
                XSSFRow row = sheet.createRow(rownum+1);
                fieldnum = 0;
                for (String fieldName : fieldNames){
                    Field f = FieldUtils.getField(first.getClass(), fieldName, true);
                    XSSFCell cell = row.createCell(fieldnum++);
                    cell.setCellStyle(createStyle(wb, f));
                    Object prop = PropertyUtils.getProperty(t, fieldName);
                    if (prop == null){ continue;}
                    if (prop instanceof Date) {
                        ExcelField annotation = f.getAnnotation(ExcelField.class);
                        String text = DateFormatUtils.format((Date)prop, annotation.pattern());
                        cell.setCellValue(text);
                    } else {
                        cell.setCellValue(prop.toString());
                    }
                }
            }
            // todo autoSizeColumn有问题，好像没有区分中文和英文的宽度
            fieldnum = 0;
            for (String fieldName: fieldNames) {
                Field f = FieldUtils.getField(first.getClass(), fieldName, true);
                if (!f.isAnnotationPresent(ExcelField.class)) {
                    continue;
                }
                ExcelField annotation = f.getAnnotation(ExcelField.class);
                int width = annotation.width();
                if (width > 0){
                    sheet.setColumnWidth(fieldnum, width);
                }
                if (width == -1){
                    sheet.autoSizeColumn(fieldnum);
                }
                fieldnum++;
            }
        }
        wb.write(out);
    }

    /**
     * 导出只有一个sheet的excel，
     * sheet中的title定义在T.class中
     * @param fieldNames 指定T.class中哪些属性需要导出
     */
    public static <T> void write(String sheetName, List<T> data, String[] fieldNames, OutputStream out) throws Exception {
        Map<String, List<T>> map = new HashMap<>(16);

        if (StringUtils.isBlank(sheetName)){
            sheetName = "Sheet1";
        }
        map.put(sheetName, data);

        writeMultiSheet(map, fieldNames, out);
    }

    /**
     * 导出只有一个sheet的excel，
     * data中第一个list做为title导出
     * */
    public static void writeMatrix(String sheetName, List<List<String>> data, OutputStream out) throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook();

        if (StringUtils.isBlank(sheetName)){
            sheetName = "Sheet1";
        }
        XSSFSheet sheet = wb.createSheet(sheetName);

        if (data == null || data.size() == 0) {
            wb.write(out);
            return;
        }

        XSSFCellStyle styleHeader = createHeaderStyle(wb);
        XSSFCellStyle styleBody = createStyle(wb);

        for (int i = 0; i < data.size(); i++) {
            List<String> cols = data.get(i);
            if (cols == null || cols.size() == 0){
                continue;
            }
            XSSFRow row = sheet.createRow(i);

            XSSFCellStyle style = (0 == i ? styleHeader : styleBody);

            int fieldnum = 0;
            for (String col: cols) {
                XSSFCell cell = row.createCell(fieldnum++);
                cell.setCellStyle(style);
                cell.setCellValue(col);
            }

        }

        wb.write(out);
    }

    /**
     * 导出只有一个sheet的excel，
     * sheet中的title定义在T.class中
     */
    public static void write(String sheetName, List data, OutputStream out) throws Exception {

        if (data == null || data.size() == 0){
            data = noData;
        }
        Object first = data.get(0);

        Field[] fields = FieldUtils.getFieldsWithAnnotation(first.getClass(), ExcelField.class);

        List<String> fieldNames = new ArrayList<>();
        for (Field f: fields){
            fieldNames.add(f.getName());
        }

        if (fieldNames.size() == 0){
            throw new IllegalArgumentException("can not found an annotation named ExcelField.class");
        }
        write(sheetName, data, fieldNames.toArray(new String[]{}), out);

    }

    private static XSSFCellStyle createStyle(XSSFWorkbook wb) {

        XSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }

    private static XSSFCellStyle createStyle(XSSFWorkbook wb, Field field) {

        XSSFCellStyle style = createStyle(wb);

        ExcelField annotation = field.getAnnotation(ExcelField.class);
        style.setAlignment(annotation.align());

        return style;
    }

    private static XSSFCellStyle createHeaderStyle(XSSFWorkbook wb) {

        XSSFCellStyle style = createStyle(wb);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);

        return style;
    }

    private static XSSFCellStyle createHeaderStyle(XSSFWorkbook wb, Field field) {

        XSSFCellStyle style = createStyle(wb, field);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);

        return style;
    }

    public static List<NoData> noData = new ArrayList<>();
    static {
        noData.add(new NoData());
    }

    public static class NoData {
        @ExcelField
        private String nodata = "无数据";

        public String getNodata() {
            return nodata;
        }

        public void setNodata(String nodata) {
            this.nodata = nodata;
        }
    }

    /**
     * 多行表头
     * dataList：导出的数据；sheetName：表头名称； head0：表头第一行列名；headnum0：第一行合并单元格的参数
     * head1：表头第二行列名；headnum1：第二行合并单元格的参数；detail：导出的表体字段
     *
     */
    public static void exportMergeXls(HttpServletRequest request,HttpServletResponse response, List<Map<String, Object>> dataList,String sheetName, String[] head0, String[] headnum0, String[] head1, String[] headnum1, String[] detail ) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(sheetName);
        // 表头标题样式
        HSSFFont headfont = workbook.createFont();
        headfont.setFontName("宋体");
        headfont.setFontHeightInPoints((short) 22);
        HSSFCellStyle headStyle = workbook.createCellStyle();
        headStyle.setFont(headfont);
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        headStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headStyle.setLocked(true);
        HSSFFont datefont = workbook.createFont();
        datefont.setFontName("宋体");
        datefont.setFontHeightInPoints((short) 12);
        HSSFCellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setFont(datefont);
        dateStyle.setAlignment(HorizontalAlignment.CENTER);
        dateStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dateStyle.setLocked(true);
        // 列名样式
        HSSFFont font = workbook.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 12);
        HSSFCellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setLocked(true);
        // 普通单元格样式（中文）
        HSSFFont font2 = workbook.createFont();
        font2.setFontName("宋体");
        font2.setFontHeightInPoints((short) 12);
        HSSFCellStyle style2 = workbook.createCellStyle();
        style2.setBorderBottom(BorderStyle.THIN);
        style2.setBorderLeft(BorderStyle.THIN);
        style2.setBorderTop(BorderStyle.THIN);
        style2.setBorderRight(BorderStyle.THIN);
        style2.setFont(font2);
        style2.setAlignment(HorizontalAlignment.CENTER);
        // 换行
        style2.setWrapText(true);
        style2.setVerticalAlignment(VerticalAlignment.CENTER);
        // 设置列宽  （第几列，宽度）
        sheet.setColumnWidth( 0, 6500);
        sheet.setColumnWidth( 1, 4000);
        sheet.setColumnWidth( 2, 2800);
        sheet.setColumnWidth( 3, 2800);
        sheet.setColumnWidth( 4, 2800);
        sheet.setColumnWidth( 5, 2800);
        sheet.setColumnWidth( 6, 2800);
        sheet.setColumnWidth( 7, 2800);
        sheet.setColumnWidth( 8, 2800);
        sheet.setColumnWidth( 9, 2800);
        sheet.setColumnWidth( 10, 2800);
        sheet.setColumnWidth( 11, 4000);
        sheet.setColumnWidth( 12, 4000);
        sheet.setColumnWidth( 13, 2800);
        sheet.setColumnWidth( 14, 2800);
        sheet.setColumnWidth( 15, 5500);
        sheet.setColumnWidth( 16, 4000);
        sheet.setColumnWidth( 17, 4000);
        sheet.setColumnWidth( 18, 2800);
        //设置行高
        sheet.setDefaultRowHeight((short)360);
        HSSFRow row = sheet.createRow(0);
        // 第1行表头列名
        HSSFRow row1 = sheet.createRow(0);
        row1.setHeight((short) 0x349);
        HSSFCell cell1 = row1.createCell(0);
        cell1.setCellStyle(headStyle);
        for (int i = 0; i < head0.length ; i++) {
            cell1 = row1.createCell(i);
            cell1.setCellValue(head0[i]);
            cell1.setCellStyle(style);
        }
        //动态合并单元格
        for (int i = 0; i < headnum0.length; i++) {
            String[] temp = headnum0[i].split(",");
            Integer startrow = Integer.parseInt(temp[0]);
            Integer overrow = Integer.parseInt(temp[1]);
            Integer startcol = Integer.parseInt(temp[2]);
            Integer overcol = Integer.parseInt(temp[3]);
            sheet.addMergedRegion(new CellRangeAddress(startrow, overrow,
                    startcol, overcol));
        }
        //设置合并单元格的参数并初始化带边框的表头（这样做可以避免因为合并单元格后有的单元格的边框显示不出来）
        //因为下标从0开始，所以这里表示的是excel中的第2行
        HSSFRow row2 = sheet.createRow(1);
        HSSFCell cell2 = row2.createCell(0);
        cell2.setCellStyle(headStyle);
        for (int i = 0; i < head0.length; i++) {
            cell2 = row2.createCell(i);
            //设置excel中第四行的1、19列的边框
            cell2.setCellStyle(style);
            if(i > 0 && i< 18) {
                for (int j = 0; j < head1.length; j++) {
                    cell2 = row2.createCell(j + 1);
                    //给excel中第四行的2~18列赋值
                    cell2.setCellValue(head1[j]);
                    //设置excel中第四行的2~18列的边框
                    cell2.setCellStyle(style);
                }
            }
        }

        // 设置列值-内容
        for (int i = 0; i < dataList.size(); i++) {
            //表头字段共占了2行，所以在填充数据的时候要加2，也就是数据要从第3行开始填充
            row1 = sheet.createRow(i + 2);
            for (int j = 0; j < detail.length; j++) {
                Map tempmap = (HashMap) dataList.get(i);
                Object data = tempmap.get(detail[j]);
                cell1 = row1.createCell(j);
                cell1.setCellStyle(style2);
                cell1.setCellType(CellType.STRING);
                CellUtil.setCellValue(cell1, data);
            }
        }
        String fileName = new String(sheetName.getBytes("gb2312"), "ISO8859-1");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        response.setContentType("application/x-download;charset=utf-8");
        response.addHeader("Content-Disposition", "attachment;filename="
                + fileName + ".xls");
        OutputStream os = response.getOutputStream();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        byte[] b = new byte[2048];
        while ((bais.read(b)) > 0) {
            os.write(b);
        }
        bais.close();
        os.flush();
        os.close();
    }

    public static void bidDataExport(String sheetName, List<List<String>> data, OutputStream out) throws Exception {

        // 在内存当中保持 100 行 , 超过的数据放到硬盘中
        SXSSFWorkbook wb = new SXSSFWorkbook(1000);
        if(StringUtils.isEmpty(sheetName)){
            sheetName = "sheet1";
        }
        Sheet sh = wb.createSheet(sheetName);
        for(int rownum = 0; rownum < data.size(); rownum++){
            Row row = sh.createRow(rownum);
            List<String> line = data.get(rownum);
            for(int cellnum = 0; cellnum < line.size(); cellnum++){
                Cell cell = row.createCell(cellnum);
                cell.setCellValue(line.get(cellnum));
            }
        }
        wb.write(out);
//        out.close();

        // dispose of temporary files backing this workbook on disk
//        wb.dispose();
    }


}
