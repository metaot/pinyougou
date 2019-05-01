package cn.itcast.core.controller.upload;
import cn.itcast.core.pojo.good.Brand;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.servlet.view.document.AbstractXlsView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.print.Book;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Tony on 2017/8/9.
 */
public class ExcelViewPostList  extends AbstractXlsView {
    @Override
    protected void buildExcelDocument(Map<String, Object> model,
                                      Workbook workbook,
                                      HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {

        // change the file name
        response.setContentType("application/x-msdownload");//返回的格式
        response.setHeader("Content-Disposition", "attachment; filename=\"PostManageList.xls\"");//返回头属性
        List<Brand> brandList = (List<Brand>) model.get("brandList ");//获取返回的数据
        Sheet sheet = workbook.createSheet("PostList Detail");// 创建sheet并命名
        sheet.setDefaultColumnWidth(30);//默认列的宽度
        // 下面是设置表头的样式
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        style.setFillForegroundColor(HSSFColor.BLUE.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        font.setBold(true);
        font.setColor(HSSFColor.WHITE.index);
        style.setFont(font);


        // 设置表头每个字段名
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("名称");
        header.getCell(0).setCellStyle(style);
        header.createCell(1).setCellValue("首字母");
        header.getCell(1).setCellStyle(style);
        header.createCell(2).setCellValue("状态");
        header.getCell(2).setCellStyle(style);


        int rowCount = 1;
        //进行赋值
        for (int i = 0; i < brandList .size(); i++) {
            Brand brand =brandList.get(i);
            Row userRow = sheet.createRow(rowCount++);
            userRow.createCell(0).setCellValue(brand.getName());
            userRow.createCell(1).setCellValue(brand.getFirstChar());
            userRow.createCell(2).setCellValue(brand.getStatus());
          //  userRow.createCell(3).setCellValue(bookList1 .getCreationDate().toString());
        }
    }

}
