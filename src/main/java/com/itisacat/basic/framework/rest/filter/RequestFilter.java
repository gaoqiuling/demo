package com.itisacat.basic.framework.rest.filter;

import com.alibaba.druid.util.DruidWebUtils;
import com.alibaba.druid.util.PatternMatcher;
import com.alibaba.druid.util.ServletPathMatcher;
import com.google.common.base.Stopwatch;
import com.itisacat.basic.framework.api.dao.IdsReset;
import com.itisacat.basic.framework.consts.PropConsts;
import com.itisacat.basic.framework.consts.SysRestConsts;
import com.itisacat.basic.framework.core.config.BaseProperties;
import com.itisacat.basic.framework.core.slowlogger.TransferSlowLogger;
import com.itisacat.basic.framework.core.util.EmptyUtils;
import com.itisacat.basic.framework.core.util.IdUtil;
import com.itisacat.basic.framework.core.util.IpUtil;
import com.itisacat.basic.framework.rest.filter.logging.RestServerLoggingUtil;
import com.itisacat.basic.framework.rest.filter.logging.ServerRequestWrapper;
import com.itisacat.basic.framework.rest.handler.ResponseHeaderProcess;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class RequestFilter extends TransferSlowLogger implements HJFilter {

    private static final String PARAM_NAME_EXCLUSIONS = "exclusions";
    private static final String UNKNOWN_STRING = "unknown";
    private long receiveTimeoutMs;
    private Set<String> excludesPattern;
    private static IdsReset idsRest;
    private String contextPath;
    
    protected PatternMatcher pathMatcher = new ServletPathMatcher();
    
    static{
        Iterator<IdsReset> iterator = ServiceLoader.load(IdsReset.class).iterator();
        if(iterator.hasNext()){
            idsRest = iterator.next();
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        Stopwatch sp = Stopwatch.createStarted();

        if (!(request instanceof HttpServletRequest && response instanceof HttpServletResponse)) {
            throw new ServletException("HttpFilter can't handle an non-http request");
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (isExclusion(httpRequest.getRequestURI())) {
            chain.doFilter(request, httpResponse);
            return;
        }

        // 多数据源复位设置
        if(idsRest != null){
            idsRest.reset();
        }
        String requestId = buildRequestId(httpRequest);
        String path = extractFullPath(httpRequest);
        // 请求时间与当前系统时间是否超过1秒
        String reqTimeStr = httpRequest.getHeader(SysRestConsts.HEADER_REQ_TIME);
        if(EmptyUtils.isNotEmpty(reqTimeStr)){
            TransferSlowLogger.markSlowLogger(Long.parseLong(reqTimeStr), receiveTimeoutMs, path, log);
        }
        try {
        	ResponseHeaderProcess.process(httpResponse);
            chain.doFilter(httpRequest, httpResponse);
        } catch (Exception e) {
    			ServerRequestWrapper requestWrapper = new ServerRequestWrapper(requestId, httpRequest);
    			
                StringBuilder sb = new StringBuilder(RestServerLoggingUtil.getInstance().getRequestMessage(requestWrapper, true));
                sb.append(StringUtils.LF);
                sb.append(String.format("%s > %s", requestId, ExceptionUtils.getStackTrace(e)));
    			log.error(sb.toString());
        } finally {
            log.info("access method [" + httpRequest.getMethod() + "]、url [{}], cost time [{}] ms )", path,
                    sp.stop().elapsed(TimeUnit.MILLISECONDS));
            MDC.clear();
        }
    }
    

    /**
     * 生成requestid 包含jvm机器ip
     * 
     * @param httpRequest
     */
    private String buildRequestId(HttpServletRequest httpRequest) {
        String requestId = MDC.get(SysRestConsts.TRACE_ID);
        if (StringUtils.isEmpty(requestId)) {
            requestId = httpRequest.getHeader(SysRestConsts.REQUEST_ID);
            if (StringUtils.isEmpty(requestId)) {
                 requestId = IdUtil.getSeqID();
            }
        }
        MDC.put(SysRestConsts.REQUEST_ID, requestId);
        MDC.put(SysRestConsts.SERVER_IP, IpUtil.getIp());
        MDC.put(SysRestConsts.CLINET_IP, getRemoteAddr(httpRequest));
        
        return requestId;
    }

    public boolean isExclusion(String incomingURI) {
        if (excludesPattern == null) {
            return false;
        }
        String requestURI = incomingURI;
        if (contextPath != null && requestURI.startsWith(contextPath)) {
            requestURI = requestURI.substring(contextPath.length());
            if (!requestURI.startsWith("/")) {
                requestURI = "/" + requestURI;
            }
        }

        for (String pattern : excludesPattern) {
            if (pathMatcher.matches(pattern, requestURI)) {
                return true;
            }
        }

        return false;
    }
    
    private String extractFullPath(HttpServletRequest req) {
        StringBuilder path = new StringBuilder(req.getScheme().trim().concat("://").concat(req.getServerName()).concat(":")
                .concat(Integer.toString(req.getServerPort())).concat(req.getRequestURI()));
        String queryStr = req.getQueryString();
        if (queryStr != null) {
            path.append("?").append(req.getQueryString());
        }
        return path.toString();
    }

//    private String getHeader(HttpServletRequest req) {
//        Enumeration<String> headerNames = req.getHeaderNames();
//        Map<String, String> headers = new HashMap<>();
//        while (headerNames.hasMoreElements()) {
//            String headerName = headerNames.nextElement();
//            headers.put(headerName, req.getHeader(headerName));
//        }
//        return headers.toString();
//    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        String exclusions = config.getInitParameter(PARAM_NAME_EXCLUSIONS);
        if (exclusions != null && exclusions.trim().length() != 0) {
            excludesPattern = new HashSet<>(Arrays.asList(exclusions.split("\\s*,\\s*")));
        }
        this.contextPath = DruidWebUtils.getContextPath(config.getServletContext());
        if("/".equals(this.contextPath)){
            this.contextPath = null;
        }
        receiveTimeoutMs = BaseProperties.getProperty(PropConsts.Rest.HTTP_RECEIVE_TIMEOUT_THRESHOLD, Long.class, 1000l);
    }

    public String getRemoteAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || UNKNOWN_STRING.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN_STRING.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN_STRING.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (EmptyUtils.isNotEmpty(ip)) {
            String[] newIp = ip.split(BaseProperties.getProperty(PropConsts.Rest.SYSTEM_REMOTEIP_SPLIT_FLAG, ","));
            return newIp[0].trim();
        }

        return ip;
    }

    @Override
    public void destroy() {
        // 老黄暂时没有实现这里的逻辑
    }

    @Override
    public int getOrder() {
        return BaseProperties.getProperty(PropConsts.Rest.FILTER_ORDER_REQUEST, Integer.class, 100);
    }

    @Override
    public String getUrlPatterns() {
        return BaseProperties.getProperty(PropConsts.Rest.REST_REQUESTILTER_IINCLUDE_URL, SysRestConsts.INCLUDE_URL_PATTEER);
    }

    @Override
    public Map<String, String> getInitParameter() {
        Map<String, String> map = new HashMap<>(1);
        map.put("exclusions", BaseProperties.getProperty(PropConsts.Rest.REST_REQUESTILTER_EXCLUSIONS_URL, SysRestConsts.EXCLUSIONS_URL_PATTEER));
        return map;
    }

}
