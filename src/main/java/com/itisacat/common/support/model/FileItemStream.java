package com.itisacat.common.support.model;

import org.apache.commons.fileupload.FileItem;

import java.io.*;

/**
 * Created by baixiaoyun on 2016/11/9.
 */

/**
 * 只实现了基本类型，如果需要其他类型，可以自定义
 */
public class FileItemStream implements FileItem {

    public FileItemStream(String fieldName, String fileName, InputStream inputStream) throws IOException {

        this(fieldName, fileName, "application/octet-stream", ((long) (inputStream.available())), inputStream);
    }

    public FileItemStream(String fieldName, String fileName, String contentType, long length, InputStream inputStream) {
        this.contentType = contentType;
        this.inputStream = inputStream;
        this.size = length;
        this.fieldName = fieldName;
        this.fileName = fileName;
    }

    public FileItemStream(String fieldName, String fileName, String contentType, InputStream inputStream) throws IOException {

        this(fieldName, fileName, contentType, ((long) (inputStream.available())), inputStream);

    }


    private final long size;
    private final String fileName;
    private final InputStream inputStream;

    private final String fieldName;

    private final String contentType;

    @Override
    public InputStream getInputStream() throws IOException {
        return inputStream;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public String getName() {
        return this.fileName;
    }

    @Override
    public boolean isInMemory() {
        return false;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public byte[] get() {
        return new byte[0];
    }

    @Override
    public String getString(String s) throws UnsupportedEncodingException {
        return null;
    }

    @Override
    public String getString() {
        return null;
    }

    @Override
    public void write(File file) throws Exception {

    }

    @Override
    public void delete() {

    }

    @Override
    public String getFieldName() {
        return this.fieldName;
        //return fileName;
    }

    @Override
    public void setFieldName(String s) {

    }

    @Override
    public boolean isFormField() {
        return false;
    }

    @Override
    public void setFormField(boolean b) {

    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return null;
    }
}
