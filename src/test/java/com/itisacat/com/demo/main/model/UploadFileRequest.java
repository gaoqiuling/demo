package com.itisacat.com.demo.main.model;

import com.itisacat.basic.framework.rest.model.BaseRequest;
import lombok.Data;

import java.util.Map;

@Data
public class UploadFileRequest extends BaseRequest {
    private Map<String, String> textMap;
    private String filePath;
    private String fileName;
}
