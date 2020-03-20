package com.itisacat.com.common.core.exception;

import com.itisacat.basic.framework.rest.model.BaseResponse;
import lombok.Data;

@Data
public class CcExcInfo implements BaseResponse {
    private Integer errorCode;

    private String msg;

    private String domain;

    public CcExcInfo(){}

    public CcExcInfo(Integer code){
        this.errorCode = code;
        this.msg = "[未定义]";
    }
}
