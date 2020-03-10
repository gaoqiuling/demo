package com.itisacat.com.common.support.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColumnMapper {
    String title() default "";   // 标题名称

    double order();      // 标题顺序

    int width() default 16;   // 单元格宽度

    boolean dynamicColumn() default false;
}
