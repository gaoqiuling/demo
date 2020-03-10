package com.itisacat.basic.framework.core.util;

import com.itisacat.basic.framework.consts.GeneralConsts;
import com.itisacat.basic.framework.consts.SysErrorConsts;
import com.itisacat.basic.framework.core.exception.SysException;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.zip.*;

/**
 * 字符串压缩、解压操作工具类
 *
 * @author lijun.hu
 *
 */
@Slf4j
public class ZipUtils {

    private static final String STR = ".";
    private static final String COMPRESSED_STRING_NOT_NULL = "The 'compressedStr' must not be null!";

    private ZipUtils() {

    }

    /**
     * 使用gzip进行压缩
     * 
     * @param primStr
     * @return
     */
    public static String gzip(String primStr) {
        Assert.notNull(primStr, "The 'primStr' must not be null!");
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(primStr.getBytes(GeneralConsts.DEFAULT_CHARSET));
        } catch (IOException e) {
            throw new SysException(SysErrorConsts.SYS_ERROR_CODE, e.getMessage(), e);
        }
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }

    /**
     * 使用gzip进行解压缩
     * 
     * @param compressedStr
     * @return
     */
    public static String gunzip(String compressedStr) {

        Assert.notNull(compressedStr, COMPRESSED_STRING_NOT_NULL);

        byte[] compressed = Base64.getDecoder().decode(compressedStr);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ByteArrayInputStream in = new ByteArrayInputStream(compressed);
                GZIPInputStream ginzip = new GZIPInputStream(in)) {

            byte[] buffer = new byte[1024];
            int offset = -1;
            while ((offset = ginzip.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
        } catch (IOException e) {
            throw new SysException(SysErrorConsts.SYS_ERROR_CODE, e.getMessage(), e);
        }

        return new String(out.toByteArray(), GeneralConsts.DEFAULT_CHARSET);
    }

    /**
     * 使用zip进行压缩
     * 
     * @param str 压缩前的文本
     * @return 返回压缩后的文本
     */
    public static final String zip(String str) {
        Assert.notNull(str, "The 'str' must not be null!");
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (ZipOutputStream zout = new ZipOutputStream(out)) {
            zout.putNextEntry(new ZipEntry("0"));
            zout.write(str.getBytes(GeneralConsts.DEFAULT_CHARSET));
        } catch (IOException e) {
            throw new SysException(SysErrorConsts.SYS_ERROR_CODE, e.getMessage(), e);
        }
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }

    public static byte[] gzip(byte[] val) throws IOException {
        byte[] result = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(val.length);
                GZIPOutputStream gos = new GZIPOutputStream(bos);) {
            gos.write(val, 0, val.length);
            gos.finish();
            gos.flush();
            bos.flush();
            result = bos.toByteArray();
        }
        return result;
    }

    /**
     * 使用zip进行解压缩
     * 
     * @param compressedStr 压缩后的文本
     * @return 解压后的字符串
     */
    public static final String unzip(String compressedStr) {
        Assert.notNull(compressedStr, COMPRESSED_STRING_NOT_NULL);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] compressed = Base64.getDecoder().decode(compressedStr);
        try (ByteArrayInputStream in = new ByteArrayInputStream(compressed);
                ZipInputStream zin = new ZipInputStream(in)) {
            zin.getNextEntry();
            byte[] buffer = new byte[1024];
            int offset = -1;
            while ((offset = zin.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
        } catch (IOException e) {
            throw new SysException(SysErrorConsts.SYS_ERROR_CODE, e.getMessage(), e);
        }
        return new String(out.toByteArray(), GeneralConsts.DEFAULT_CHARSET);
    }


    @SuppressWarnings("unchecked")
    public static final File[] unzip4j(String source, String destination, String password) {
        try {
            ZipFile zipFile = new ZipFile(source);
            // 设置编码方式
            zipFile.setFileNameCharset(GeneralConsts.DEFAULT_CHARSET.name());

            // 判断文件是否有效
            if (!zipFile.isValidZipFile()) {
                throw new ZipException("压缩文件不合法,可能被损坏.");
            }
            File destDir = new File(destination);

            if (destDir.isDirectory() && !destDir.exists()) {
                destDir.mkdir();
            }
            // 解密
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password.toCharArray());
            }
            // 获取文件头
            List<FileHeader> headerList = zipFile.getFileHeaders();
            List<File> extractedFileList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(headerList)) {
                // 空文件夹操作
                if (headerList.size() == 1) {
                    FileHeader fileHeader = headerList.get(0);
                    if (fileHeader.isDirectory() || fileHeader.getFileName().isEmpty()) {
                        extractedFileList.add(new File(destDir, fileHeader.getFileName()));
                        zipFile.extractFile(fileHeader, destination);
                    }
                } else {
                    // 非空文件夹操作
                    for (FileHeader fileHeader : headerList) {
                        if (fileHeader.isDirectory() || fileHeader.getFileName().isEmpty()) {
                            // 对路径不做任何操作
                        } else {
                            // 提取文件
                            extractedFileList.add(new File(destDir, fileHeader.getFileName()));
                            zipFile.extractFile(fileHeader, destination);
                        }

                    }
                }
            }
            File[] extractedFiles = new File[extractedFileList.size()];
            extractedFileList.toArray(extractedFiles);
            return extractedFiles;
        } catch (Exception e) {
            log.error("ZipUtils:unzip4j", e);
            throw new SysException(SysErrorConsts.SYS_ERROR_CODE, e.getMessage(), e);
        }
    }


    public static String zip4j(String source, String destination, boolean isCreateDir, String password) {
        // 创建文件
        File srcFile = new File(source);
        // 创建路径
        String destnation = buildDestinationZipFilePath(srcFile, destination);
        // 指定压缩方式和密码
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); // 压缩方式
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL); // 压缩级别

        if (!StringUtils.isEmpty(password)) {
            // 设置密码
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD); // 加密级别
            parameters.setPassword(password.toCharArray());
        }

        try {
            ZipFile zipFile = new ZipFile(destnation);
            if (srcFile.isDirectory()) {
                // 如果不创建目录的话,将直接把给定目录下的文件压缩到压缩文件,即没有目录结构
                if (!isCreateDir) {
                    File[] subFiles = srcFile.listFiles();
                    ArrayList<File> temp = new ArrayList<>();
                    Collections.addAll(temp, subFiles);
                    zipFile.addFiles(temp, parameters);
                    return destnation;
                }
                // 添加到zip文件夹
                zipFile.addFolder(srcFile, parameters);
            } else {
                zipFile.addFile(srcFile, parameters);
            }
            return destnation;
        } catch (ZipException e) {
            // do something
            log.warn("zip4j-compress Exception", e);
        }
        return null;
    }


    private static String buildDestinationZipFilePath(File srcFile, String destParam) {
        String destUrl = destParam;
        if (StringUtils.isEmpty(destParam)) {
            if (srcFile.isDirectory()) {
                destUrl = srcFile.getParent() + File.separator + srcFile.getName() + ".zip";
            } else {
                String fileName = srcFile.getName().substring(0, srcFile.getName().lastIndexOf(STR));
                destUrl = srcFile.getParent() + File.separator + fileName + ".zip";
            }
        } else {
            createDestDirectoryIfNecessary(destUrl); // 在指定路径不存在的情况下将其创建出来
            if (destUrl.endsWith(File.separator)) {
                String fileName = "";
                if (srcFile.isDirectory()) {
                    fileName = srcFile.getName();
                } else {
                    fileName = srcFile.getName().substring(0, srcFile.getName().lastIndexOf(STR));
                }
                destUrl += fileName + ".zip";
            }
        }
        return destUrl;
    }


    private static void createDestDirectoryIfNecessary(String destParam) {
        File destDir = null;
        if (destParam.endsWith(File.separator)) {
            destDir = new File(destParam);
        } else {
            destDir = new File(destParam.substring(0, destParam.lastIndexOf(File.separator)));
        }
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
    }

}
