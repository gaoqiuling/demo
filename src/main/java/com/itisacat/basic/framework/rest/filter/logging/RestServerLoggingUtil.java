package com.itisacat.basic.framework.rest.filter.logging;

import com.itisacat.basic.framework.consts.PropConsts;
import com.itisacat.basic.framework.core.config.BaseProperties;
import com.itisacat.basic.framework.core.httpclient.logger.AbstractRestLogging;
import com.itisacat.basic.framework.core.httpclient.logger.RestRequestWrapper;
import com.itisacat.basic.framework.core.httpclient.wrapper.HttpRequestWrapper;
import com.itisacat.basic.framework.core.httpclient.wrapper.HttpResponseWrapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;


@Slf4j
public class RestServerLoggingUtil extends AbstractRestLogging {

    private RestServerLoggingUtil() {

    }

    private static class SingletonHolder {

        private SingletonHolder() {
        }

        private static final RestServerLoggingUtil INSTANCE = new RestServerLoggingUtil();
    }

    public static RestServerLoggingUtil getInstance() {
        return RestServerLoggingUtil.SingletonHolder.INSTANCE;
    }


    public boolean shouldRestLogging(ServletRequest request) {
        try {
            if (request instanceof HttpServletRequest) {
                return shouldLog(((HttpServletRequest) request).getRequestURI());
            }
        } catch (Exception e) {
            log.error("Server ShouldRestLogging Error : {}", e);
        }
        return false;
    }

    public RestRequestWrapper restRequestLog(ServletRequest request) {
        try {
            if (!(request instanceof HttpRequestWrapper)) {
                return null;
            }
            HttpRequestWrapper requestToUse = (HttpRequestWrapper) request;
            return requestLogDetail(requestToUse);
        } catch (Exception e) {
            log.error("Server Request Log Error : {}", e);
        }
        return null;
    }

    public void restResponseLog(RestRequestWrapper restRequestWrapper, ServletResponse response) {
        try {
            if (restRequestWrapper == null) {
                return;
            }
            HttpRequestWrapper requestToUse = restRequestWrapper.getRequestToUse();
            HttpResponseWrapper responseToUse = (HttpResponseWrapper)response;

            responseLogDetail(restRequestWrapper, responseToUse);
        } catch (Exception e) {
            log.error("Server Response Log Error : {}", e);
        }
    }

    @Override
    protected boolean enableRequestResponseLog() {
        return BaseProperties.getProperty(PropConsts.Rest.HTTP_SERVER_LOG_ENABLE, Boolean.class, false);
    }




    /**
     * 获取当前请求消息
     * @param request
     * @return
     */
    public String getRequestMessage(HttpRequestWrapper request,boolean hasOutputBody) {
        StringBuilder sbMsg = new StringBuilder();
        try {
            sbMsg.append(  getAfterMessage(request,hasOutputBody) );
        } catch (Exception e) {
            log.error("Server getContextMessage Log Error : {}", e);
        }
        return sbMsg.toString();
    }
}
