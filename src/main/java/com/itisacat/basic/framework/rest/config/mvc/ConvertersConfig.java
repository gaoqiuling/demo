package com.itisacat.basic.framework.rest.config.mvc;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.google.common.collect.Lists;
import com.itisacat.basic.framework.consts.PropConsts;
import com.itisacat.basic.framework.core.config.BaseProperties;
import com.itisacat.basic.framework.core.fastjson.DateCodec;
import com.itisacat.basic.framework.core.util.DateUtil;
import com.itisacat.basic.framework.core.util.EmptyUtils;
import com.itisacat.basic.framework.core.util.SpecharsUtil;
import com.itisacat.basic.framework.core.util.StringUtil;
import com.itisacat.basic.framework.rest.http.HttpBodyHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configuration
public class ConvertersConfig {

    @Autowired(required = false)
    private IConvertersHandler handler;

    @Autowired(required = false)
    private ResultHandler resultHandler;

    @Autowired(required = false)
    private HttpBodyHandler bodyHandler;

    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        HJFastJsonHttpMessageConverter fastConverter = new HJFastJsonHttpMessageConverter(resultHandler, bodyHandler);
        FastJsonConfig fastJsonConfig = new FastJsonConfig();

        List<MediaType> mediaTypeList = new ArrayList<>();
        MediaType jsonType = new MediaType("application", "json", Charset.forName("utf-8"));
        mediaTypeList.add(jsonType);


        MediaType xwwwformUrlencodedType =
                new MediaType("application", "x-www-form-urlencoded", Charset.forName("utf-8"));
        mediaTypeList.add(xwwwformUrlencodedType);

        mediaTypeList.add(MediaType.TEXT_PLAIN);
        fastConverter.setSupportedMediaTypes(mediaTypeList);
        
        fastJsonConfig.setDateFormat(DateUtil.HUJIANG_DATE_FORMAT);

        if (handler != null) {
            handler.process(fastJsonConfig);
        }
        List<SerializerFeature> listFeature = Lists.newArrayList();
        String serializerFeatureStr = BaseProperties.getString(PropConsts.Rest.MVC_SERIALIZERFEATURE);
        if (EmptyUtils.isNotEmpty(serializerFeatureStr)) {
            String[] features = StringUtil.split(serializerFeatureStr, SpecharsUtil.SYMBOL_COMMA);
            for (String feature : features) {
                listFeature.add(SerializerFeature.valueOf(feature));
            }
        }
        fastJsonConfig.setSerializerFeatures(listFeature.toArray(new SerializerFeature[listFeature.size()]));

        // 增强fastjson对日期格式处理
        ParserConfig parserConfig = new ParserConfig();
        parserConfig.putDeserializer(Date.class, DateCodec.instance);
        fastJsonConfig.setParserConfig(parserConfig);

        fastConverter.setFastJsonConfig(fastJsonConfig);
        HttpMessageConverter<?> converter = fastConverter;
        return new HttpMessageConverters(converter);
    }

}
