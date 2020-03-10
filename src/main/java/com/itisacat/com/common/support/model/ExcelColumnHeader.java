package com.itisacat.com.common.support.model;


public class ExcelColumnHeader implements Comparable<ExcelColumnHeader> {

    private String title; // 标题名称
    private double order; // 标题顺序
    private int width; // 宽度
    private String methodName; // 对应方法名称
    private Long arg;

    public ExcelColumnHeader(String title, double order, int width, String methodName, Long arg) {
        super();
        this.width = width;
        this.title = title;
        this.order = order;
        this.methodName = methodName;
        this.arg = arg;
    }

    public int compareTo(ExcelColumnHeader o) {
        return order > o.order ? 1 : (order < o.order ? -1 : 0);
    }

    public String getTitle() {
        return title;
    }

    public double getOrder() {
        return order;
    }

    public String getMethodName() {
        return methodName;
    }

    public int getWidth() {
        return width;
    }

    public Long getArg() {
        return arg;
    }
}
