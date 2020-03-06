/**
 * 
 */
package com.itisacat.basic.framework.dao.route;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;

/**
 * 动态数据路由
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {
	    
		return DataSourceSwitch.getDataSource();
	}
	
	public DataSource getCurrentDataSource(){
	    return determineTargetDataSource();
	}
}
