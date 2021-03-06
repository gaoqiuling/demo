package com.itisacat.com.common.core.config;

import com.alibaba.fastjson.TypeReference;
import com.itisacat.basic.framework.core.util.JsonUtil;
import com.itisacat.com.common.core.exception.CcExcInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 通用异常配置.
 * 描述：
 * 通过配置文件处理异常文本信息
 *
 * @author LIJUN.HU - (FOX)
 * @create 2017-01-09 14:38
 * Copyright (c) 2016 沪江教育科技（上海）股份有限公司
 */
@Slf4j
@Configuration
public class CcExceptionConfig {
    private final String ERROR_CONFIG_FILE= "/ccexception.json";
    private static Map<Integer, CcExcInfo> vMap = new TreeMap<>();


    @Bean
    public Map<Integer, CcExcInfo> ExecBean(){
        read(ERROR_CONFIG_FILE);
        return vMap;
    }

    private synchronized boolean read(String file){
        StringBuilder result = new StringBuilder();
        vMap = new TreeMap<>();
        try{
            InputStream inputStream= getClass().getResourceAsStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String s = null;
            while((s = br.readLine())!=null){
                result.append(System.lineSeparator()+s);
            }
            br.close();
            List<CcExcInfo> items =  JsonUtil.json2Reference(result.toString(),new TypeReference<List<CcExcInfo>>(){});
            items.stream().forEach(n->{
                vMap.put(n.getErrorCode(),n);
            });
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static CcExcInfo getError(Integer code){
        if(vMap.containsKey(code)){
            return vMap.get(code);
        }
        return new CcExcInfo(code);
    }
}
