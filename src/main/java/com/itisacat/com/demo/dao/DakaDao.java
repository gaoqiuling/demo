package com.itisacat.com.demo.dao;


import com.itisacat.basic.framework.dao.annotation.MyBatisRepository;

@MyBatisRepository
public interface DakaDao {
    String getDakaName(Integer dakaId);
}