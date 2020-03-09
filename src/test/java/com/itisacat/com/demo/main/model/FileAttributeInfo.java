package com.itisacat.com.demo.main.model;

import com.itisacat.basic.framework.rest.model.BaseResponse;
import lombok.Data;

@Data
public class FileAttributeInfo implements BaseResponse {
    private String url;
    private Integer width;
    private Integer height;
    private Integer fileSize;
    private String fileType;
    private String imageData;
    private String imageId;
}
