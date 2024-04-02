package com.sqds.filelutil.excelutil;

import org.apache.poi.hssf.record.BOFRecord;

import java.lang.annotation.*;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExceLTitle {
    /**
     * 标题
     * @return
     */
    String title() default "";

    String column() default  "";
    boolean isperenage() default false;
}
