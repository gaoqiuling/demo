package com.itisacat.com.demo.main;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.itisacat.basic.framework.rest.model.DataResult;
import com.itisacat.com.demo.main.model.FileUploadResponse;
import com.itisacat.com.demo.main.model.UploadFileRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
@ComponentScan("com.itisacat.common")
@Slf4j
public class UploadImageTest {
    @Test
    public void testExcel() {
        UploadFileRequest request = new UploadFileRequest();
        request.setFilePath("e:\\my\\demo\\test.xls");
        request.setFileName("test");

        DataResult<FileUploadResponse> result = new DataResult<>();

        URI uri = null;
        try {
            uri = new URI("http://qaccfile.hjapi.com/file/v1.1/upload");
        } catch (URISyntaxException e) {
            log.error(e.getMessage());
        }
        ClientHttpRequest httpRequest = null;
        try {
            httpRequest = new SimpleClientHttpRequestFactory().createRequest(uri, HttpMethod.POST);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        String boundary = "Uploader" + (new Date()).getTime();
        httpRequest.getHeaders().add("Content-Type", "multipart/form-data; boundary=----" + boundary);

        //普通字串
        String fmtform = "\r\n------%s\r\nContent-Disposition: form-data; name=\"%s\"\r\n\r\n%s";
        //文件
        String fmtfile = "\r\n------%s\r\nContent-Disposition: form-data; name=\"file\"; filename=\"%s\"\r\nContent-Type: %s\r\n\r\n";
        //结尾
        String fmtend = "\r\n------%s--\r\n";

        byte[] bytes;

        OutputStream bOutputStream = null;
        InputStream input = null;
        DataInputStream in = null;
        ClientHttpResponse response = null;
        File file = null;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            bOutputStream = httpRequest.getBody();
            if (request.getTextMap() != null && request.getTextMap().size() > 0) {
                for (Map.Entry<String, String> key : request.getTextMap().entrySet()) {
                    bytes = String.format(fmtform, boundary, key.getKey(), key.getValue()).getBytes("UTF8");
                    bOutputStream.write(bytes, 0, bytes.length);
                }
            }
            file = new File(request.getFilePath());
            bytes = String.format(fmtfile, boundary,
                    StringEscapeUtils.escapeHtml4(file.getName()), "application/octet-stream")
                    .getBytes("UTF8");
            bOutputStream.write(bytes, 0, bytes.length);
            input = new FileInputStream(file);
            byte[] bufferOut = new byte[input.available()];
            in = new DataInputStream(input);
            int byt = 0;
            while ((byt = in.read(bufferOut)) != -1) {
                bOutputStream.write(bufferOut, 0, byt);
            }

            //写入结尾
            bytes = String.format(fmtend, boundary).getBytes("UTF8");
            bOutputStream.write(bytes, 0, bytes.length);

            //开始上传
            response = httpRequest.execute();
            byte[] buff = new byte[1024];
            //从字符串获取字节写入流
            int len = -1;
            while (-1 != (len = response.getBody().read(buff))) {
                //将字节数组转换为字符串
                String res = new String(buff, 0, len);
                stringBuilder.append(res);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            result.setStatus(-4000);
            result.setMessage(e.getMessage());
        } finally {
            if (response != null) {
                response.close();
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
            try {
                if (bOutputStream != null) {
                    bOutputStream.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
            Boolean isDelete = file != null && file.exists() && file.delete();
        }
        result = JSONObject.parseObject(stringBuilder.toString(), new TypeReference<DataResult<FileUploadResponse>>() {
        });
        System.out.println(result.getData().getInfo().get(0).getUrl());
    }
}
