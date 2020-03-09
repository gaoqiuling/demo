package com.itisacat.com.demo.main;

import com.google.common.collect.Lists;
import com.itisacat.common.support.model.ColumeNameEntity;
import com.itisacat.common.support.model.ColumnIdEntity;
import com.itisacat.common.util.ExcelUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@ComponentScan("com.itisacat.common")
public class ExcelTest {
    @Test
    public void testExcel() {
        StudentRequest s1 = new StudentRequest();
        s1.setUserId(1);
        s1.setName("name1");
        s1.setColumns(Arrays.asList(new ColumnIdEntity(1l, 100l)));

        StudentRequest s2 = new StudentRequest();
        s2.setUserId(2);
        s2.setName("name2");
        s2.setColumns(Arrays.asList(new ColumnIdEntity(1l, 30l), new ColumnIdEntity(2l, 20l)));
        List<StudentRequest> list = Arrays.asList(s1, s2);

        ColumeNameEntity columnName1 = new ColumeNameEntity(1l, "qiuqiu1");
        ColumeNameEntity columnName2 = new ColumeNameEntity(2l, "qiuqiu2");
        List<ColumeNameEntity> columeNames = Arrays.asList(columnName1, columnName2);
        ExcelUtil.saveExcel(new File("").getAbsolutePath() + "/test.xls",
                list, StudentRequest.class,
                "test", 50000, columeNames);
    }
}
