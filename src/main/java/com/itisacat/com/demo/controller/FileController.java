package com.itisacat.com.demo.controller;

import com.itisacat.basic.framework.core.exception.AppException;
import com.itisacat.basic.framework.core.util.DateUtil;
import com.itisacat.basic.framework.core.util.EmptyUtils;
import com.itisacat.basic.framework.rest.model.DataResult;
import com.itisacat.com.demo.support.model.request.UploadRequest;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequestMapping(value = {"/file/v1.1/", "/file/v1/"})
@Slf4j
public class FileController {
    /**
     * 上传文件
     *
     * @param req
     * @param
     * @return
     */
    @CrossOrigin(maxAge = 3600)
    @RequestMapping(value = "upload", method = RequestMethod.POST)
    public DataResult<String> upload(UploadRequest request, HttpServletResponse response, HttpServletRequest req) {
        MultipartHttpServletRequest defaultMultipartHttpServletRequest = (MultipartHttpServletRequest) req;
        if (request.getFile() == null || request.getFile().size() == 0) {
            final MultiValueMap<String, MultipartFile> file = defaultMultipartHttpServletRequest.getMultiFileMap();
            if (file.size() > 0) {
                request.setFile(new ArrayList<>());
                request.getFile().add(file.toSingleValueMap().entrySet().iterator().next().getValue());
            }
        }
        if (EmptyUtils.isEmpty(request.getFile())) {
            throw new AppException(-40001, "file is not  null");
        }
        return DataResult.ok("");
    }

    @ApiOperation(value = "test", httpMethod = "GET", response = DataResult.class, notes = "test")
    @RequestMapping(value = "test", method = RequestMethod.GET)
    public DataResult<Date> test() {
        return DataResult.ok(DateUtil.currentDate());
    }
}