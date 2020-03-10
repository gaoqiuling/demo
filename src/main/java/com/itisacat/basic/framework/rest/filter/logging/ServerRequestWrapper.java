package com.itisacat.basic.framework.rest.filter.logging;

import com.google.common.collect.Maps;
import com.itisacat.basic.framework.core.httpclient.wrapper.HttpRequestWrapper;
import org.apache.commons.io.input.TeeInputStream;
import org.apache.http.client.config.RequestConfig;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.MultiValueMap;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * @Author: dylan
 * @Date: 2019-02-15 12:35
 * @Desc:
 */
public class ServerRequestWrapper extends HttpServletRequestWrapper implements HttpRequestWrapper {

    private static final String[] BINARY_TYPES_PREFIX = {"image", "video", "audio"};

    private static final String MULTIPART_FORM_DATA_VALUE = "multipart/form-data";

    private final ByteArrayOutputStream bos = new ByteArrayOutputStream();

    private String id;

    private boolean read;

    private MultiValueMap<String, String> headers;

    private Map<String, Object> paramData;

    public ServerRequestWrapper(String requestId, HttpServletRequest request) {
        super(request);
        this.id = requestId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] toByteArray() {
        return bos.toByteArray();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (!read) {
            read = true;
            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return false;
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setReadListener(ReadListener readListener) {

                }

                private TeeInputStream tee = new TeeInputStream(ServerRequestWrapper.super.getInputStream(), bos);

                @Override
                public int read() throws IOException {
                    return tee.read();
                }
            };
        } else {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(toByteArray());

            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return false;
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setReadListener(ReadListener readListener) {

                }

                @Override
                public int read() throws IOException {
                    return byteArrayInputStream.read();
                }
            };
        }


    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getRequestURLString() {
        try {
            return getRequestURL().toString();
        } catch (Exception e) {
            return getRequestURI();
        }
    }

    @Override
    public MultiValueMap<String, String> getHeaders() {
        if (headers == null) {
            headers = new ServletServerHttpRequest(this).getHeaders();
        }
        return headers;
    }

    @Override
    public boolean isBinaryContent() {
        String contentType = getContentType();

        if (contentType == null) {
            return true;
        }

        if (!contentType.toLowerCase().contains("json")) {
            return true;
        }


        for (String binaryTypePrefix : BINARY_TYPES_PREFIX) {
            if (contentType.startsWith(binaryTypePrefix)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isMultipart() {
        String contentType = getContentType();
        return contentType != null && contentType.startsWith(MULTIPART_FORM_DATA_VALUE);
    }

	

	@Override
	public RequestConfig getRequestConfig() {
		
		return this.getRequestConfig();
	}

	@Override
	public URI getURI() {
		return this.getURI();
	}

	@Override
	public void setHeader(String name, String value) {
		setHeader(name, value);

	}

	@Override
	public void addParamData(String key, Object data) {
		if(paramData == null) {
			paramData = Maps.newHashMap();
		}
		paramData.put(key, data);
	}

    @Override
    public String getContentTypeString() {
        return getContentType();
    }


    @Override
	public Map<String, Object> getAllParamData() {
		return paramData;
	}



}
