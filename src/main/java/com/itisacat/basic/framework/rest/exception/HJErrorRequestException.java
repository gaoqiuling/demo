package com.itisacat.basic.framework.rest.exception;

import com.itisacat.basic.framework.core.exception.AppException;

/**
 * 说明：一般请求错误 场景：由客户端引起的通用错误
 * 
 * @author huangxin
 *
 */
public class HJErrorRequestException extends AppException {

    private static final long serialVersionUID = 3085259429306084335L;

    private static final int ERROR_CODE = 40000;

    public HJErrorRequestException(String msg) {
        super(ERROR_CODE, msg);
    }

    public int getCode() {
        return ERROR_CODE;
    }

}
