package cn.itcast.core.utils.xls;

import org.apache.poi.hssf.usermodel.HSSFCell;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;

/**
 * @author gaopeng
 */
public class CellUtil {

    /**
     * 避免cell.setCellValue(checkOrderQmSave.getSellOrderNo())中参数为空就会报错
     * @param cell
     * @param object
     */
    public static void setCellValue(HSSFCell cell, Object object){
        if(object == null){
            cell.setCellValue("");
        }else{
            if (object instanceof String) {
                cell.setCellValue(String.valueOf(object));
            }else if(object instanceof Long){
                Long temp = (Long)object;
                String cellvalue = new DecimalFormat("#0.00").format(temp.doubleValue());
                cell.setCellValue(cellvalue);
            }else if(object instanceof Double){
                Double temp = (Double)object;
                String cellvalue = new DecimalFormat("#0.00").format(temp.doubleValue());
                cell.setCellValue(cellvalue);
            }else if(object instanceof Float){
                Float temp = (Float)object;
                String cellvalue = new DecimalFormat("#0.00").format(temp.doubleValue());
                cell.setCellValue(cellvalue);
            }else if(object instanceof Integer){
                Integer temp = (Integer)object;
                cell.setCellValue(temp.intValue());
            }else if(object instanceof BigDecimal){
                BigDecimal temp = (BigDecimal)object;
                String cellvalue = new DecimalFormat("#0.00").format(temp.doubleValue());
                cell.setCellValue(cellvalue);
            }else{
                cell.setCellValue("");
            }
        }
    }
    public static void setCellValue(HSSFCell cell, Object object, String model){
        if(object == null){
            cell.setCellValue("");
        }else{
            if (object instanceof String) {
                cell.setCellValue(String.valueOf(object));
            }else if(object instanceof Long){
                Long temp = (Long)object;
                String cellvalue = new DecimalFormat("#0.00").format(temp.doubleValue());
                cell.setCellValue(cellvalue);
            }else if(object instanceof Double){
                Double temp = (Double)object;
                String cellvalue = new DecimalFormat("#0.00").format(temp.doubleValue());
                cell.setCellValue(cellvalue);
            }else if(object instanceof Float){
                Float temp = (Float)object;
                String cellvalue = new DecimalFormat("#0.00").format(temp.doubleValue());
                cell.setCellValue(cellvalue);
            }else if(object instanceof Integer){
                Integer temp = (Integer)object;
                cell.setCellValue(temp.intValue());
            }else if(object instanceof BigDecimal){
                BigDecimal temp = (BigDecimal)object;
                String cellvalue = new DecimalFormat("#0.00").format(temp.doubleValue());
                cell.setCellValue(cellvalue);
            }else if(object instanceof java.sql.Date){
                cell.setCellValue(new SimpleDateFormat(model).format(object));
            }else if(object instanceof Date){
                cell.setCellValue(new SimpleDateFormat(model).format(object));
            }else{
                cell.setCellValue("");
            }
        }
    }
    public static void setCellValue(HSSFCell cell, String object){
        if(object == null){
            cell.setCellValue("");
        }else{
            cell.setCellValue(object);
        }
    }
    public static void setCellValue(HSSFCell cell, Long object){
        if(object == null){
            cell.setCellValue("");
        }else{
            cell.setCellValue(object.doubleValue());
        }
    }
    public static void setCellValue(HSSFCell cell, Double object){
        if(object == null){
            cell.setCellValue("");
        }else{
            cell.setCellValue(object.doubleValue());
        }
    }
    public static void setCellValue(HSSFCell cell, double object){

        cell.setCellValue(object);

    }
    public static void setCellValue(HSSFCell cell, Date object, String model){
        if(object == null){
            cell.setCellValue("");
        }else{
            cell.setCellValue(new SimpleDateFormat(model).format(object));
        }
    }
    public static void setCellValue(HSSFCell cell, BigDecimal object){
        if(object == null){
            cell.setCellValue("");
        }else{
            cell.setCellValue(object.toString());
        }
    }


    /**
     * 判断EXCEL表格高度用 row.setHeight((short) CellUtil.getExcelCellAutoHeight(TAR_VAL_ALL_STRING, 280, 30));
     * @param str
     * @param defaultRowHeight
     * @param fontCountInline
     * @return
     */
    public static float getExcelCellAutoHeight(String str,float defaultRowHeight, int fontCountInline) {
        int defaultCount = 0;
        for (int i = 0; i < str.length(); i++) {
            int ff = getregex(str.substring(i, i + 1));
            defaultCount = defaultCount + ff;
        }
        if (defaultCount > fontCountInline){
            return ((int) (defaultCount / fontCountInline) + 1) * defaultRowHeight;
        } else {
            return defaultRowHeight;
        }
    }
    public static int getregex(String charStr) {
        if("".equals(charStr) || charStr == null){
            return 1;
        }
        // 判断是否为字母或字符
        String wordReg = "^[A-Za-z0-9]+$";
        String chinaReg = "[\u4e00-\u9fa5]+$";;
        String chinaAllReg = "[^x00-xff]";

        if (compile(wordReg).matcher(charStr).matches()) {
            return 1;
        }
        // 判断是否为全角
        if (compile(chinaReg).matcher(charStr).matches()) {
            return 2;
        }
        //全角符号 及中文
        if (compile(chinaAllReg).matcher(charStr).matches()) {
            return 2;
        }
        return 1;
    }
}
