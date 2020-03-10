package com.itisacat.basic.framework.api.dao;


/**
 * DB多数据源切换复位处理，因为多数据的dbname 采用ThreadLocal存储，任何一次新请求需要复位处理
 *
 *
 */
public interface IdsReset {
    void reset();
}
