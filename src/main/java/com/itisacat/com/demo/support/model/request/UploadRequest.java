package com.itisacat.com.demo.support.model.request;

import com.itisacat.basic.framework.rest.model.BaseRequest;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UploadRequest extends BaseRequest {
    private String Type;
    private List<MultipartFile> file;
    private String args;
    private String path;
    private String ext;
    /***是否获取静止图片***/
    private Integer showQuiet;
    private String fileName;
}
