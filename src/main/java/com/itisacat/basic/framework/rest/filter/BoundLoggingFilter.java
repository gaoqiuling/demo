package com.itisacat.basic.framework.rest.filter;

import com.itisacat.basic.framework.consts.PropConsts;
import com.itisacat.basic.framework.consts.SysRestConsts;
import com.itisacat.basic.framework.core.config.BaseProperties;
import com.itisacat.basic.framework.core.httpclient.logger.RestLoggingId;
import com.itisacat.basic.framework.core.httpclient.logger.RestRequestWrapper;
import com.itisacat.basic.framework.rest.filter.logging.RestServerLoggingUtil;
import com.itisacat.basic.framework.rest.filter.logging.ServerRequestWrapper;
import com.itisacat.basic.framework.rest.filter.logging.ServerResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class BoundLoggingFilter implements HJFilter {

    @Override
    public int getOrder() {
        return 120;
    }

    @Override
    public String getUrlPatterns() {
        return BaseProperties.getProperty(PropConsts.Rest.REST_REQUESTILTER_IINCLUDE_URL, SysRestConsts.INCLUDE_URL_PATTEER);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        RestRequestWrapper restRequestWrapper = null;
        ServletRequest filterRequest = servletRequest;
        ServletResponse filterResponse = servletResponse;
        try {
            if (RestServerLoggingUtil.getInstance().shouldRestLogging(servletRequest)) {
                LoggingFilterInner loggingFilterInner = new LoggingFilterInner(servletRequest, servletResponse).invoke();
                restRequestWrapper = loggingFilterInner.getRestRequestWrapper();
                filterRequest = loggingFilterInner.getFilterRequest();
                filterResponse = loggingFilterInner.getFilterResponse();
            }

            filterChain.doFilter(filterRequest, filterResponse);
        } finally {
            if (restRequestWrapper != null) {
                RestServerLoggingUtil.getInstance().restResponseLog(restRequestWrapper, filterResponse);
            }
        }
    }

    @Override
    public void destroy() {

    }

    private class LoggingFilterInner {

        private ServletRequest servletRequest;

        private ServletResponse servletResponse;

        private RestRequestWrapper restRequestWrapper;

        private ServletRequest filterRequest;

        private ServletResponse filterResponse;

        public LoggingFilterInner(ServletRequest servletRequest, ServletResponse servletResponse) {
            this.servletRequest = servletRequest;
            this.servletResponse = servletResponse;
        }

        public RestRequestWrapper getRestRequestWrapper() {
            return restRequestWrapper;
        }

        public ServletRequest getFilterRequest() {
            return filterRequest;
        }

        public ServletResponse getFilterResponse() {
            return filterResponse;
        }

        public LoggingFilterInner invoke() {
            filterRequest = servletRequest;
            filterResponse = servletResponse;
            try {
                String logId = String.valueOf(RestLoggingId.incrementAndGet());
                filterRequest = new ServerRequestWrapper(logId, (HttpServletRequest) servletRequest);
                filterResponse = new ServerResponseWrapper(logId, (HttpServletResponse) servletResponse);
                restRequestWrapper = RestServerLoggingUtil.getInstance().restRequestLog(filterRequest);

            } catch (Exception e) {
                log.error("Server BoundLoggingFilter Error : {}", e);
            }
            return this;
        }
    }
}

