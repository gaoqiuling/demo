package com.itisacat.com.demo.main.model;

import com.itisacat.basic.framework.rest.model.BaseResponse;
import lombok.Data;

import java.util.List;

@Data
public class FileUploadResponse implements BaseResponse {
    private List<FileAttributeInfo> info;
    private int count;
}
