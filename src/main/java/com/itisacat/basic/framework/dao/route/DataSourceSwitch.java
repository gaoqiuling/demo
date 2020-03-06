/**
 *
 */
package com.itisacat.basic.framework.dao.route;

import com.itisacat.basic.framework.dao.model.DataSourceMeta;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


/**
 * 数据源切换
 */
@Slf4j
public class DataSourceSwitch {
    private static final ThreadLocal<DataSourceMeta> holder = new ThreadLocal<>();
    private static final List<String> dataSourceIds = new ArrayList<>();

    public static void setDataSource(String name) {
        DataSourceMeta meta = getMeta();
        meta.setDsName(name);
        holder.set(meta);
        log.debug("Datasouce change: {}.", name);
    }

    public static void setDataType(String type) {
        DataSourceMeta meta = getMeta();
        meta.setDsType(type);
        holder.set(meta);
    }

    private static DataSourceMeta getMeta() {
        DataSourceMeta meta = holder.get();
        if (meta == null) {
            meta = new DataSourceMeta();
        }
        return meta;
    }

    public static String getDataSource() {
        return getMeta().getDsName();
    }

    public static String getDataType() {
        return getMeta().getDsType();
    }

    /**
     * 判断指定DataSrouce当前是否存在
     *
     * @param dataSourceId
     * @return
     * @author SHANHY
     * @create 2016年1月24日
     */
    public static boolean containsDataSource(String dataSourceId) {
        return dataSourceIds.contains(dataSourceId);
    }


    public static void addDataSource(String dataSourceId) {
        dataSourceIds.add(dataSourceId);
    }

    public static void clearDataSource() {
        holder.remove();
    }

    public void reset() {
        holder.remove();
    }
}
