package com.itisacat.basic.framework.rest.http;

import org.springframework.http.HttpHeaders;

/**
 * 对于应用中想处理http body内容处理，实现此接口，然后注入spring ioc即可
 * 
 * @author xinhuang
 *
 */
public interface HttpBodyHandler {

    /**
	 * 在接收请求后，在序列化成controller中参数对象之前。
	 * @param requestBody 请求时body内容，格式为json
	 * @return
	 */
	String requestHandler(final String requestBody, HttpHeaders header);

    /**
	 * 在处理完成后，通过返回对象序列化后。
	 * @param responseHeader 响应时的header内容。可以在响应后添加头部信息
	 * @return
	 */
	void responseHandler(HttpHeaders responseHeader);
}
