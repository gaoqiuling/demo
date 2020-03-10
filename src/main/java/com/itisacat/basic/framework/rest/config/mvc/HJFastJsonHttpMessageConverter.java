package com.itisacat.basic.framework.rest.config.mvc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter4;
import com.google.common.io.CharStreams;
import com.itisacat.basic.framework.consts.GeneralConsts;
import com.itisacat.basic.framework.core.util.EmptyUtils;
import com.itisacat.basic.framework.core.util.ZipUtils;
import com.itisacat.basic.framework.rest.config.compress.CompressHandler;
import com.itisacat.basic.framework.rest.exception.HJErrorRequestException;
import com.itisacat.basic.framework.rest.http.HttpBodyHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;

public class HJFastJsonHttpMessageConverter extends FastJsonHttpMessageConverter4 {

    /**
     * with fastJson config
     */
    private FastJsonConfig fastJsonConfig;

    private ResultHandler resultHandler;

    private HttpBodyHandler bodyHandler;

    private static final String HEADER_GZIP_STR = "gzip";
    private static final String HEADER_CONTENT_ENCODING = "Content-Encoding";

    public HJFastJsonHttpMessageConverter(ResultHandler resultHandler, HttpBodyHandler bodyHandler) {
        super();
        this.resultHandler = resultHandler;
        this.bodyHandler = bodyHandler;
    }

    /**
     * @return the fastJsonConfig.
     */
    @Override
    public FastJsonConfig getFastJsonConfig() {
        return fastJsonConfig;
    }

    /**
     * @param fastJsonConfig the fastJsonConfig to set.
     */
    @Override
    public void setFastJsonConfig(FastJsonConfig fastJsonConfig) {
        this.fastJsonConfig = fastJsonConfig;
    }

    @Override
    public Object read(Type type, //
                       Class<?> contextClass, //
                       HttpInputMessage inputMessage //
    ) throws IOException {

        return inRead(type, inputMessage);
    }

    @Override
    protected Object readInternal(Class<? extends Object> clazz, //
                                  HttpInputMessage inputMessage //
    ) throws IOException {

        return inRead(clazz, inputMessage);
    }

    @Override
    protected void writeInternal(Object obj, Type type, HttpOutputMessage outputMessage) throws IOException {
        try {
            try (ByteArrayOutputStream outnew = new ByteArrayOutputStream()) {
                HttpHeaders headers = outputMessage.getHeaders();
                FastJsonConfig jsonConfig = getFastJsonConfig();
                SerializeConfig serializeConfig = jsonConfig.getSerializeConfig();

                if (EmptyUtils.isEmpty(serializeConfig)) {
                    serializeConfig = new SerializeConfig();
                }
                String result = JSON.toJSONString(obj, serializeConfig, jsonConfig.getSerializeFilters(),
                        jsonConfig.getDateFormat(), JSON.DEFAULT_GENERATE_FEATURE, jsonConfig.getSerializerFeatures());

                if (resultHandler != null && resultHandler.getValue() != null) { // 对JSONP的处理
                    result = resultHandler.process(result, headers);
                }

                byte[] content = gzipIfNecessary(result, headers); // 压缩处理

                if (bodyHandler != null) {
                    bodyHandler.responseHandler(headers);
                }

//                getHeaderProcess().process(headers); //系统级对Response header处理

                headers.setContentLength(content.length);
                outnew.write(content);

                OutputStream out = outputMessage.getBody();
                outnew.writeTo(out);

            }
        } catch (Exception e) {
            JSONObject json = new JSONObject();
            json.put("errorMsg", "Response body deserialize to JSON String Failed! Case:" + e.getMessage());
            throw new HJErrorRequestException(json.toJSONString());
        }
    }

    private Object inRead(Type type, HttpInputMessage inputMessage) throws IOException {
        HttpHeaders header = inputMessage.getHeaders();

        InputStream inputStream = inputMessage.getBody();

        String contentEncoding = CompressHandler.getContentEncoding();
        if (contentEncoding != null && contentEncoding.contains(HEADER_GZIP_STR)) {
            inputStream = new GZIPInputStream(inputStream);
        }

        MediaType contentType = header.getContentType();
        Charset charset = (contentType.getCharset() != null ? contentType.getCharset() : GeneralConsts.DEFAULT_CHARSET);
        String contentStr = CharStreams.toString(new InputStreamReader(inputStream, charset));

        if (contentType.isCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED)) {
            try {
                contentStr = bodyChangeJson(contentStr, charset);
            } catch (Exception e) {
                JSONObject json = new JSONObject();
                json.put("errorMsg", MediaType.APPLICATION_FORM_URLENCODED + " Change to Json Failed!");
                throw new HJErrorRequestException(json.toJSONString());
            }
        }
        if (bodyHandler != null) {
            contentStr = bodyHandler.requestHandler(contentStr, header);
        }
        try {
            return JSON.parseObject(contentStr, type, fastJsonConfig.getParserConfig());
        } catch (Exception e) {
            JSONObject json = new JSONObject();
            json.put("paramData", contentStr);
            json.put("errorMsg", "Request param(type:json) serialization to Object Failed! Case: " + e.getMessage());
            throw new HJErrorRequestException(json.toJSONString());
        }
    }

    private boolean isGzipReponseAccepted() {
        String acceptEncoding = CompressHandler.getAcceptEncoding();
        return (acceptEncoding != null && acceptEncoding.contains(HEADER_GZIP_STR));
    }

    private byte[] gzipIfNecessary(String result, HttpHeaders headers) throws IOException {
        byte[] originBytes = result.getBytes(GeneralConsts.DEFAULT_CHARSET);

        if (isGzipReponseAccepted()) {
            byte[] gzipped = ZipUtils.gzip(originBytes);
            headers.set(HEADER_CONTENT_ENCODING, HEADER_GZIP_STR);
            return gzipped;
        } else {
            return originBytes;
        }
    }

    private String bodyChangeJson(String body, Charset charset) throws UnsupportedEncodingException {
        String[] pairs = StringUtils.tokenizeToStringArray(body, "&");
        JSONObject json = new JSONObject();
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            if (idx != -1) {
                String name = URLDecoder.decode(pair.substring(0, idx), charset.name());
                String value = URLDecoder.decode(pair.substring(idx + 1), charset.name());
                json.put(name, value);
            }
        }
        return json.toJSONString();
    }
}
