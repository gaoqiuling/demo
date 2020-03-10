package com.itisacat.basic.framework.core.httpclient.logger;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.itisacat.basic.framework.consts.PropConsts;
import com.itisacat.basic.framework.core.config.BaseProperties;
import com.itisacat.basic.framework.core.exception.AppException;
import com.itisacat.basic.framework.core.httpclient.wrapper.HttpRequestWrapper;
import com.itisacat.basic.framework.core.httpclient.wrapper.HttpResponseWrapper;
import com.itisacat.basic.framework.core.util.JsonUtil;
import com.itisacat.basic.framework.rest.model.DataResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import static org.apache.commons.lang3.StringUtils.*;


@Slf4j
public abstract class AbstractRestLogging {

    private static final int DEFAULT_MAX_PAYLOAD_LENGTH = 6 * 1024;

    private static final String ROOT_PATH = "/";

    /**
     * The Constant REQUEST_PREFIX.
     */
    private static final String REQUEST_PREFIX = "> ";

    /**
     * The Constant RESPONSE_PREFIX.
     */
    private static final String RESPONSE_PREFIX = "< ";

    /**
     * The constant LOGGING_REQUEST_NOTE.
     */
    private static final String LOGGING_REQUEST_NOTE = "Server received a request";

    /**
     * The constant LOGGING_RESPONSE_NOTE.
     */
    private static final String LOGGING_RESPONSE_NOTE = "Server responded with a response";

    /**
     * The Constant NOTIFICATION_PREFIX.
     */
    private static final String NOTIFICATION_PREFIX = "* ";

    /**
     * The Constant ON_THREAD
     */
    private static final String ON_THREAD = " on thread ";

    /**
     * The Constant COMMA
     */
    private static final String COMMA = ",";

    /**
     * The Constant HEADER_SPLITTER
     */
    private static final String HEADER_SPLITTER = ": ";

    /**
     * The constant QUESTION.
     */
    private static final String QUESTION = "?";

    /**
     * The constant BODY_BYTE_PREFIX.
     */
    private static final String BODY_BYTE_PREFIX = "(";

    /**
     * The constant BODY_BYTE_SUFFIX.
     */
    private static final String BODY_BYTE_SUFFIX = "-byte body)";

    /**
     * The constant ELAPSEDTIME_PREFIX.
     */
    private static final String ELAPSEDTIME_PREFIX = "(";

    /**
     * The constant ELAPSEDTIME_SUFFIX.
     */
    private static final String ELAPSEDTIME_SUFFIX = "ms)";

    /**
     * 超出实体内容最大值展现的消息
     */
    private static final String EXCEEDED_MESSAGE = " ...more...";


    private static final String[] BINARY_TYPES_PREFIX = {"image", "video", "audio"};

    private static final String PAGE_SUFFIX = ".html";

    private static final String HEADER_ACCEPT_TAG = "Accept";

    private static final String JSON_TAG = "json";

    /**
     * The Constant COMPARATOR.
     */
    private static final Comparator<Map.Entry<String, List<String>>> COMPARATOR = ((o1, o2) -> o1.getKey().compareToIgnoreCase(o2.getKey()));

    private static final AtomicLong ID = new AtomicLong(0);

    private static final Pattern P1 = Pattern.compile("^(?:\\d+\\s{1}<\\s{1}HTTP/.+\\s{1}(404\\s{1}.+)\\(\\d+ms\\))$");

    private static final Pattern P2 = Pattern.compile("^(?:\\d+\\s{1}<\\s{1}\\((0)\\-byte\\s{1}body\\))$");

    private static final String SERVER_FLAG = "Server";

    private static final String CLIENT_FLAG = "Client";

    private int maxPayloadLength = DEFAULT_MAX_PAYLOAD_LENGTH;

    /**
     * Return the maximum length of the payload body to be included in the log message.
     *
     * @since 3.0
     */
    private int getMaxPayloadLength() {
        return this.maxPayloadLength;
    }


    /**
     * Set the maximum length of the payload body to be included in the log message.
     * Default is 50 characters.
     *
     * @since 3.0
     */
    public void setMaxPayloadLength(int maxPayloadLength) {
        Assert.isTrue(maxPayloadLength >= 0, "'maxPayloadLength' should be larger than or equal to 0");
        this.maxPayloadLength = maxPayloadLength;
    }

    private transient Logger inboundLog = LoggerFactory.getLogger("com.hujiang.basic.framework.rest.filter.logging.inbound");

    private transient Logger outboundLog = LoggerFactory.getLogger("com.hujiang.basic.framework.rest.filter.logging.outbound");

    private List<String> excludedPrefixes = Lists.newArrayList("/do_not_delete/health_check", "/healthcheck/ping");

    private List<String> excludedSuffixes = Lists.newArrayList(".html", ".js", ".js.map", ".css", ".jpg", ".png", ".gif");

    private void refreshParams() {
        String excludedPrefixesStr = BaseProperties.getProperty(PropConsts.Rest.HTTP_REQUEST_LOG_EXCLUDEDPREFIXES, String.class);
        String excludedSuffixesStr = BaseProperties.getProperty(PropConsts.Rest.HTTP_REQUEST_LOG_EXCLUDEDSUFFIXES, String.class);
        if (StringUtils.isNotEmpty(excludedPrefixesStr)) {
            excludedPrefixes = Splitter.on(",").splitToList(excludedPrefixesStr);
        }
        if (StringUtils.isNotEmpty(excludedSuffixesStr)) {
            excludedSuffixes = Splitter.on(",").splitToList(excludedSuffixesStr);
        }
    }

    protected RestRequestWrapper requestLogDetail(HttpRequestWrapper requestToUse) {
        long startTime = System.currentTimeMillis();
        boolean hasOutputBody = hasOutputResponseBody(requestToUse);
        //默认输出请求body，内部会进一步判断是否输出body
        afterRequest(getAfterMessage(requestToUse,true));
        return new RestRequestWrapper(requestToUse, startTime, hasOutputBody);
    }

    /**
     * 判断Content-Type\Accept ，只要设置有值且不为json，则不输出响应body
     * @param requestToUse
     * @return
     */
    protected boolean hasOutputResponseBody(HttpRequestWrapper requestToUse) {
        String contentTypeString = requestToUse.getContentTypeString();
        if (StringUtils.isNotEmpty(contentTypeString) && !contentTypeString.equals("none") && !contentTypeString.contains(JSON_TAG)) {
            return false;
        }


        MultiValueMap<String, String> headers = requestToUse.getHeaders();
        if (MapUtils.isNotEmpty(headers)) {

            if (headers.containsKey(HEADER_ACCEPT_TAG)) {
                String acceptString = headers.get(HEADER_ACCEPT_TAG).stream().filter(p -> p.toLowerCase().contains(JSON_TAG)).findFirst().orElse(null);
                if (acceptString == null) {
                    return false;
                }

            }
        }

        return true;
    }


    protected void responseLogDetail(RestRequestWrapper restRequestWrapper, HttpResponseWrapper responseToUse) {
        if (restRequestWrapper == null) {
            return;
        }

        HttpRequestWrapper requestToUse = restRequestWrapper.getRequestToUse();
        long elapsedTime = System.currentTimeMillis() - restRequestWrapper.getStartTime();


        if (responseToUse.getStatus() != HttpStatus.SC_NOT_FOUND) {
            beforeResponse(getBeforeMessage(responseToUse, elapsedTime, restRequestWrapper.getHasOutputBody()));
        } else {
            DataResult wrappedEntity = onRestError(new AppException(-1, String.format("Not Found Error: %s - %s", requestToUse.getMethod(), requestToUse.getRequestURLString())));
            String payloadStr = JsonUtil.object2JSON(wrappedEntity);
            String message = getBeforeMessage(responseToUse, elapsedTime, restRequestWrapper.getHasOutputBody());
            StringBuilder messageBuilder = new StringBuilder();
            Splitter.on(LF).split(message).forEach(line -> {
                Matcher matcher1 = (P1.matcher(line));
                Matcher matcher2 = (P2.matcher(line));
                if (matcher1.matches()) {
                    messageBuilder.append(line.replace(matcher1.group(1), HttpStatus.SC_OK + SPACE + "OK" + SPACE)).append(LF);
                } else if (matcher2.matches()) {
                    messageBuilder.append(line.replace(matcher2.group(1), String.valueOf(payloadStr.getBytes().length))).append(LF);
                } else {
                    messageBuilder.append(line).append(LF);
                }
            });
            messageBuilder.insert(messageBuilder.length() - 1, payloadStr);
            beforeResponse(messageBuilder.toString());
        }

    }

    private static DataResult onRestError(Throwable t) {
        int httpStatusCode = -1;
        DataResult standardJsonResponseEntity = new DataResult();
        standardJsonResponseEntity.setStatus(httpStatusCode);
        standardJsonResponseEntity.setMessage(t.getMessage());
        return standardJsonResponseEntity;
    }

    /**
     * Get the message to write to the log before the response.
     *
     * @param response
     * @param elapsedTime
     * @return
     * @see #createResponseMessage
     */
    protected String getBeforeMessage(HttpResponseWrapper response, long elapsedTime, boolean hasOutputBody) {
        return createResponseMessage(response, elapsedTime, hasOutputBody);
    }

    /**
     * Get the message to write to the log after the request.
     *
     * @see #createRequestMessage
     */
    protected String getAfterMessage(HttpRequestWrapper request,boolean hasOutputRequestBody) {
        return createRequestMessage(request,hasOutputRequestBody);
    }


    protected String createRequestMessage(HttpRequestWrapper request,boolean hasOutputRequestBody) {
        StringBuilder msg = new StringBuilder();
        printRequestLine(
                msg,
                getLoggingRequestNote(),
                request.getId(),
                request.getMethod(),
                request.getRequestURLString(),
                request.getQueryString(),
                request.getProtocol()
        );
        printPrefixedHeaders(
                msg,
                request.getId(),
                getRequestPrefix(),
                request.getHeaders()
        );
        if(hasOutputRequestBody) {
            logInboundPayload(msg, request);
        }

        return msg.toString();
    }

    private String createResponseMessage(HttpResponseWrapper response, long elapsedTime, boolean hasOutputBody) {
        StringBuilder msg = new StringBuilder();
        printResponseLine(
                msg,
                getLoggingResponseNote(),
                response.getId(),
                response.getStatus(),
                EMPTY,
                elapsedTime
        );


        printPrefixedHeaders(
                msg,
                response.getId(),
                getResponsePrefix(),
                response.getHeaders()
        );

        if (hasOutputBody) {
            logOutboundPayload(msg, response);
        }
        return msg.toString();
    }

    /**
     * 打印请求行
     *
     * @param b           the b 日志Builder
     * @param note        the note 前置标注
     * @param id          the ID 批次ID
     * @param method      the method GET/POST/PUT/DELETE
     * @param uri         the uri
     * @param queryParams
     * @param protocol
     */
    private void printRequestLine(StringBuilder b,
                                  String note,
                                  String id,
                                  String method,
                                  String uri,
                                  String queryParams,
                                  String protocol) {
        prefixId(b, id)
                .insert(0, LF)
                .append(NOTIFICATION_PREFIX)
                .append(note)
                .append(ON_THREAD)
                .append(Thread.currentThread().getName())
                .append(LF);
        prefixId(b, id)
                .append(getRequestPrefix())
                .append(method)
                .append(SPACE)
                .append(uri)
                .append(StringUtils.isEmpty(queryParams) ? EMPTY : QUESTION.concat(queryParams))
                .append(SPACE)
                .append(protocol)
                .append(LF);
    }

    /**
     * 打印响应行
     *
     * @param b           the b 日志Builder
     * @param note        the note 前置标注
     * @param id          the ID 批次ID
     * @param status      the status HTTP RESPONSE CODE
     * @param protocol
     * @param elapsedTime
     */
    private void printResponseLine(StringBuilder b,
                                   String note,
                                   String id,
                                   int status,
                                   String protocol,
                                   long elapsedTime) {
        prefixId(b, id)
                .insert(0, LF)
                .append(NOTIFICATION_PREFIX)
                .append(note)
                .append(ON_THREAD)
                .append(Thread.currentThread().getName())
                .append(LF);
        prefixId(b, id)
                .append(getResponsePrefix())
                .append(protocol)
                .append(SPACE)
                .append(Integer.toString(status))
                .append(SPACE)
                .append(Integer.toString(status))
                .append(SPACE)
                .append(ELAPSEDTIME_PREFIX)
                .append(elapsedTime)
                .append(ELAPSEDTIME_SUFFIX)
                .append(LF);
    }

    /**
     * 打印http headers.
     *
     * @param b       the b 日志Builder
     * @param id      the ID 批次ID
     * @param prefix  the prefix 前缀
     * @param headers the headers http headers
     */
    private void printPrefixedHeaders(
            StringBuilder b,
            String id,
            String prefix,
            MultiValueMap<String, String> headers) {

        getSortedHeaders(headers.entrySet()).forEach(headerEntry -> {
            List<?> val = headerEntry.getValue();
            String header = headerEntry.getKey();
            if (val.size() == 1) {
                prefixId(b, id).append(prefix).append(header).append(HEADER_SPLITTER).append(val.get(0)).append(LF);
            } else {
                StringBuilder sb = new StringBuilder();
                val.forEach(s -> sb.append(s).append(COMMA));
                sb.delete(sb.length() - 1, sb.length());
                prefixId(b, id).append(prefix).append(header).append(HEADER_SPLITTER).append(sb.toString()).append(LF);
            }
        });
    }

    /**
     * 获取排序后的http headers
     *
     * @param headers the headers http headers
     * @return the sorted headers
     */
    private Set<Map.Entry<String, List<String>>> getSortedHeaders(Set<Map.Entry<String, List<String>>> headers) {
        TreeSet<Map.Entry<String, List<String>>> sortedHeaders = new TreeSet<>(COMPARATOR);
        sortedHeaders.addAll(headers);
        return sortedHeaders;
    }

    /**
     * 打印ID前缀
     *
     * @param b  the b
     * @param id the ID
     * @return the string builder
     */
    private StringBuilder prefixId(StringBuilder b, String id) {
        b.append(id).append(SPACE);
        return b;
    }

    /**
     * 打印Payload前缀
     *
     * @param b             the b
     * @param id            the ID
     * @param prefix        the prefix
     * @param payloadPrefix the payload prefix
     */
    private void printPrefixPayload(
            StringBuilder b,
            String id,
            String prefix,
            String payloadPrefix) {
        prefixId(b, id).append(prefix).append(payloadPrefix);
    }


    /**
     * 记录入站负载内容日志
     *
     * @param b
     * @param request
     */
    private void logInboundPayload(StringBuilder b, HttpRequestWrapper request) {
        if (!request.isMultipart() && !request.isBinaryContent()) {
            try {
                String charEncoding = request.getCharacterEncoding() != null ? request.getCharacterEncoding() :
                        Charsets.UTF_8.name();

                byte[] bytes = IOUtils.toByteArray(request.getInputStream());
                bytes = getFormatBytes(bytes, false);

                int bodyLength = bytes == null ? 0 : bytes.length;
                printPrefixPayload(b, request.getId(), getRequestPrefix(), BODY_BYTE_PREFIX.concat(Integer.toString(bodyLength)).concat(BODY_BYTE_SUFFIX).concat(LF));
                if (bodyLength == 0) {
                    return;
                }
                b.append(getStreamBody(charEncoding, bytes, bodyLength));
                if (bodyLength > getMaxPayloadLength()) {
                    b.append(EXCEEDED_MESSAGE);
                }
            } catch (Exception e) {
                log.warn("Failed to parse request payload", e);
            }
        } else {
            printPrefixPayload(b, request.getId(), getRequestPrefix(), ("(Binary Data)").concat(LF));
        }
    }


    /**
     * 记录出站负载内容日志
     *
     * @param b
     * @param response
     */
    private void logOutboundPayload(StringBuilder b, HttpResponseWrapper response) {


        try {
            String charEncoding = response.getCharacterEncoding() != null ? response.getCharacterEncoding() :
                    Charsets.UTF_8.name();
            byte[] bytes = response.toByteArray();
            bytes = getFormatBytes(bytes, response.getHasGizp());
            int bodyLength = bytes == null ? 0 : bytes.length;
            printPrefixPayload(b, response.getId(), getResponsePrefix(), BODY_BYTE_PREFIX.concat(Integer.toString(bodyLength)).concat(BODY_BYTE_SUFFIX).concat(LF));
            if (bodyLength == 0) {
                return;
            }
            b.append(getStreamBody(charEncoding, bytes, bodyLength));
            if (bodyLength > getMaxPayloadLength()) {
                b.append(EXCEEDED_MESSAGE);
            }
        } catch (Exception e) {
            log.warn("Failed to parse response payload", e);
        }

    }

    private String getStreamBody(String charEncoding, byte[] bytes, int bodyLength) throws UnsupportedEncodingException {
        return new String(bytes, 0, Math.min(bodyLength, getMaxPayloadLength()), charEncoding);
    }

    private byte[] getFormatBytes(byte[] bytes, boolean isGzip) {
        if (isGzip) {
            ByteArrayOutputStream out = null;
            try {
                InputStream inputMessage = new ByteArrayInputStream(bytes);
                out = new ByteArrayOutputStream();

                GZIPInputStream ungzip = new GZIPInputStream(inputMessage);
                byte[] buffer = new byte[256];
                int n;
                while ((n = ungzip.read(buffer)) >= 0) {
                    out.write(buffer, 0, n);
                }
                ungzip.close();

                bytes = out.toByteArray();

            } catch (IOException e) {
                log.warn("Failed to getStreamBody", e);
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        log.warn("Failed to getStreamBody close", e);
                    }
                }
            }
        }
        return bytes;
    }


    protected boolean shouldLog(String requestURLString) {
        refreshParams();

        return (outboundLog.isDebugEnabled() || inboundLog.isDebugEnabled()) && logRequest(requestURLString) && enableRequestResponseLog();
    }

    private boolean logRequest(String requestURLString) {

        if (ROOT_PATH.equals(requestURLString)) {
            return false;
        }
        //前缀处理
        if (!CollectionUtils.isEmpty(excludedPrefixes)) {
            long count = excludedPrefixes.stream().filter(ep -> StringUtils.startsWith(requestURLString, ep)).count();
            if (count > 0) {
                return false;
            }
        }
        //后缀处理
        if (!CollectionUtils.isEmpty(excludedSuffixes)) {
            long count = excludedSuffixes.stream().filter(ep -> StringUtils.endsWith(requestURLString, ep)).count();
            if (count > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Writes a log message before the response is processed.
     */
    protected void beforeResponse(String message) {
        outboundLog.debug(message);
    }

    /**
     * Writes a log message after the request is processed.
     */
    private void afterRequest(String message) {
        inboundLog.debug(message);
    }

    protected String getLoggingRequestNote() {
        return LOGGING_REQUEST_NOTE;
    }

    protected String getLoggingResponseNote() {
        return LOGGING_RESPONSE_NOTE;
    }

    protected String getRequestPrefix() {
        return REQUEST_PREFIX;
    }

    protected String getResponsePrefix() {
        return RESPONSE_PREFIX;
    }

    protected boolean enableRequestResponseLog() {
        return false;
    }
}

