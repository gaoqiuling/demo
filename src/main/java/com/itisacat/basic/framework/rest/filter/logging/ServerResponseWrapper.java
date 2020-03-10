package com.itisacat.basic.framework.rest.filter.logging;

import com.itisacat.basic.framework.core.httpclient.wrapper.HttpResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.output.TeeOutputStream;
import org.apache.http.Header;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

/**
 * @Author: dylan
 * @Date: 2019-02-15 12:36
 * @Desc:
 */
@Slf4j
public class ServerResponseWrapper extends HttpServletResponseWrapper implements HttpResponseWrapper {

    private final ByteArrayOutputStream bos = new ByteArrayOutputStream();

    private PrintWriter writer = new PrintWriter(bos);

    private String id;

    private MultiValueMap<String, String> headers;

    public ServerResponseWrapper(String requestId, HttpServletResponse response) {
        super(response);
        this.id = requestId;
    }


    public void setId(String id) {
        this.id = id;
    }

    @Override
    public ServletResponse getResponse() {
        return this;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {

            }

            private TeeOutputStream tee = new TeeOutputStream(ServerResponseWrapper.super.getOutputStream(), bos);

            @Override
            public void write(int b) throws IOException {
                tee.write(b);
            }
        };
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new TeePrintWriter(super.getWriter(), writer);
    }

    @Override
    public byte[] toByteArray() {
        return bos.toByteArray();
    }

    @Override
    public String getId() {
        return id;
    }


    @Override
    public MultiValueMap<String, String> getHeaders() {
        if (headers == null) {
            headers = new HttpHeaders();
            Collection<String> headerNames = this.getHeaderNames();
            if (!CollectionUtils.isEmpty(headerNames)) {
                headerNames.stream().forEach(headerName -> {
                    headers.add(headerName, this.getHeader(headerName));
                });
            }
        }
        return headers;
    }

    @Override
    public boolean getHasGizp() {
        try {
            MultiValueMap<String, String> headerMap = getHeaders();
            String key = "Content-Encoding";
            if(headerMap.containsKey(key)){
                return headerMap.get(key).stream().anyMatch(p->p.equalsIgnoreCase("gzip"));
            }
        } catch (Exception e) {
            log.error("Client Log getHasGizp error: {}",e);
        }
        return false;
    }

    @Override
    public Header[] getAllHeaders() {
        return this.getAllHeaders();
    }
}

