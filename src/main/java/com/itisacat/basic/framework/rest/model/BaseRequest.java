package com.itisacat.basic.framework.rest.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BaseRequest implements java.io.Serializable {

    private static final long serialVersionUID = 8976588576075723075L;
    private static final String UNKNOWN_URL = "UNKNOWN-URL";

    /**
     * 需要请求的目标Api地址
     */
    @JSONField(serialize = false)
    private String _url;

    /**
     * Api版本号
     */
    @JSONField(serialize = false)
    private Integer _version;

    /**
     * 请求源（Web、Android、Ios...）
     */
    private String _source;

    /**
     * APi请求头
     */
    @JSONField(serialize = false)
    private Map<String, String> _headers = new HashMap<>();

    
    private String baseCompany;
    private Integer baseCompanyId;
    
    public String getUrl() {
        return _url;
    }

    public String getPath() {
        if (_url == null) {
            return null;
        }
        URI uri;
        try {
            uri = new URI(_url);
        } catch (URISyntaxException e) {
            log.error("Parse url to URI failed, url: {}", _url, e);
            return UNKNOWN_URL;
        }
        return uri.getPath();
    }
    

    public void setUrl(String url) {
        this._url = url;
    }

    public Integer getVersion() {
        return _version;
    }

    public void setVersion(Integer version) {
        this._version = version;
    }

    public String getSource() {
        return _source;
    }

    public void setSource(String source) {
        this._source = source;
    }

    public Map<String, String> getHeaders() {
        return _headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this._headers = headers;
    }

    public BaseRequest() {
        this._headers = new HashMap<>();
    }

    @JSONField(serialize = false)
    public Map<String, Object> getParamData() {
        Map<String, Object> mp = new HashMap<>();

        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                mp.put(field.getName(), field.get(this));
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
        mp.remove("serialVersionUID");
        return mp;
    }

	public String getBaseCompany() {
		return baseCompany;
	}
	
	public Integer baseSellerId;

	public Integer getBaseSellerId() {
		return baseSellerId;
	}

	public void setBaseSellerId(Integer baseSellerId) {
		this.baseSellerId = baseSellerId;
	}

	public void setBaseCompany(String baseCompany) {
		this.baseCompany = baseCompany;
	}

	public Integer getBaseCompanyId() {
		return baseCompanyId;
	}

	public void setBaseCompanyId(Integer baseCompanyId) {
		this.baseCompanyId = baseCompanyId;
	}
}
