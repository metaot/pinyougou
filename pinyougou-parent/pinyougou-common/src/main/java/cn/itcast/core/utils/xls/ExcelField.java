package cn.itcast.core.utils.xls;

import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Administrator on 2016/10/13.
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelField {

    String title() default "";

    HorizontalAlignment align() default HorizontalAlignment.LEFT;

    boolean bold() default false;

    String pattern() default "yyyy-MM-dd";

    int width() default 0;

}
