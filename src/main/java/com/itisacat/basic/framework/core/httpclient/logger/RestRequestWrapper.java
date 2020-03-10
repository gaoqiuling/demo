package com.itisacat.basic.framework.core.httpclient.logger;

import com.itisacat.basic.framework.core.httpclient.wrapper.HttpRequestWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class RestRequestWrapper {

    private HttpRequestWrapper requestToUse;

    private Long startTime;

    private Boolean hasOutputBody;
}
