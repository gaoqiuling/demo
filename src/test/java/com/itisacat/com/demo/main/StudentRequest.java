package com.itisacat.com.demo.main;

import com.itisacat.basic.framework.core.util.EmptyUtils;
import com.itisacat.common.support.model.ColumnIdEntity;
import com.itisacat.common.support.model.ExcelColumnMapper;
import lombok.Data;

import java.util.List;

@Data
public class StudentRequest {

    private List<ColumnIdEntity> columns;
    private Integer userId;
    private String name;

    @ExcelColumnMapper(title = "用户id", order = 6)
    public Integer getUserIdExcel() {
        return userId;
    }

    @ExcelColumnMapper(title = "真实姓名", order = 7)
    public String getNameExcel() {
        return name;
    }


    @ExcelColumnMapper(order = 8, dynamicColumn = true)
    public Long getColumnsExcel(Long id) {
        if (EmptyUtils.isEmpty(columns)) {
            return null;
        }
        ColumnIdEntity columnIdEntity = columns.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);
        if (columnIdEntity != null) {
            return columnIdEntity.getValue();
        }
        return null;
    }
}
