package com.itisacat.basic.framework.rest.config.mvc;

import org.springframework.http.HttpHeaders;


public interface ResultHandler {
    String process(String data, HttpHeaders headers);

    String getValue();

    void setValue(Object data);

    void deleteValue();

}
