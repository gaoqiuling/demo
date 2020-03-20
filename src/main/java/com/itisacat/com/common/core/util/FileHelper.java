package com.itisacat.com.common.core.util;


import com.itisacat.basic.framework.core.config.BaseProperties;
import com.itisacat.basic.framework.core.exception.AppException;
import com.itisacat.com.common.core.model.FileItemStream;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;

@Log4j2
public class FileHelper {
    private static final Map<String, String> FILE_TYPE_MAP = new HashMap();
    private static final Integer BYTELENGTH = 1024;
    private static final CloseableHttpClient httpclient = HttpClients.createDefault();
    private static final Integer MAXSIZE = Integer.parseInt(BaseProperties.getString("file.img.max.size"));

    static {
        FILE_TYPE_MAP.put("image/jpeg", ".jpg");
        FILE_TYPE_MAP.put("image/jpg", ".jpg");
        FILE_TYPE_MAP.put("application/x-bmp", ".bmp");
        FILE_TYPE_MAP.put("image/gif", ".gif");
        FILE_TYPE_MAP.put("image/png", ".png");
    }

    //    public static BufferedImage readImageIO(InputStream image) {
//        BufferedImage imageTemp = null;
//        try {
//            imageTemp = ImageIO.read(image);
//        } catch (Exception ex) {
//            try {
//                imageTemp =
//                        JPEGCodec.createJPEGDecoder(image).decodeAsBufferedImage();
//            } catch (IOException e) {
//                WeixinPlugin.getInstance().send(String.format("convert image error:", e.getMessage()));
//                log.error(String.format("convert image error:%s,details:%s", e.getMessage(), ExceptionUtils.getStackTrace(e)));
//            }
//        }
//        return imageTemp;
//    }
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        try {
            int transparency = Transparency.OPAQUE;
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null),
                    image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }
        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            bimage = new BufferedImage(image.getWidth(null),
                    image.getHeight(null), type);
        }
        // Copy image to buffered image
        Graphics g = bimage.createGraphics();
        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bimage;
    }

    public static BufferedImage readImageIO(File image) {
        BufferedImage imageTemp = null;
        ImageInputStream input = null;
        ImageReader reader = null;
        try {
            input = ImageIO.createImageInputStream(image);
            Iterator readers = ImageIO.getImageReaders(input);
            if (readers == null || !readers.hasNext()) {
                throw new RuntimeException("1 No ImageReaders found");
            }
            reader = (ImageReader) readers.next();
            reader.setInput(input);
            String format = reader.getFormatName();

            if ("JPEG".equalsIgnoreCase(format) || "JPG".equalsIgnoreCase(format)) {//jpeg 有cmyk格式，必须这样处理
                try {
                    // 尝试读取图片 (包括颜色的转换).
                    imageTemp = reader.read(0); //RGB
                } catch (IIOException e) {
                    // 读取Raster (没有颜色的转换).
                    Raster raster = reader.readRaster(0, null);//CMYK
                    imageTemp = createJPEG4(raster);
                }
            } else {

                input.close();

                Image imageTookit = Toolkit.getDefaultToolkit().getImage(image.getPath()); //此方法解决上传图片变红色问题
                imageTemp = toBufferedImage(imageTookit);
            }

        } catch (Exception ex) {
            try {
                imageTemp = ImageIO.read(image);//如果无法转换，使用该方法
            } catch (Exception e) {
                log.error("readimage fail:{}", e);
            }
        } finally {
            try {
                if (reader != null)
                    reader.dispose();
                if (input != null)
                    input.close();
            } catch (IOException e) {
                log.error("close inputstream eror");
            }
        }
        return imageTemp;
    }

    private static BufferedImage createJPEG4(Raster raster) {
        int w = raster.getWidth();
        int h = raster.getHeight();
        byte[] rgb = new byte[w * h * 3];
        //彩色空间转换
        float[] Y = raster.getSamples(0, 0, w, h, 0, (float[]) null);
        float[] Cb = raster.getSamples(0, 0, w, h, 1, (float[]) null);
        float[] Cr = raster.getSamples(0, 0, w, h, 2, (float[]) null);
        float[] K = raster.getSamples(0, 0, w, h, 3, (float[]) null);
        for (int i = 0, imax = Y.length, base = 0; i < imax; i++, base += 3) {
            double k = 220 - K[i], y = 255 - Y[i], cb = 255 - Cb[i],
                    cr = 255 - Cr[i];

            double val = y + 1.402 * (cr - 128) - k;
            val = (val - 128) * .65f + 128;
            rgb[base] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff
                    : (byte) (val + 0.5);

            val = y - 0.34414 * (cb - 128) - 0.71414 * (cr - 128) - k;
            val = (val - 128) * .65f + 128;
            rgb[base + 1] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff
                    : (byte) (val + 0.5);

            val = y + 1.772 * (cb - 128) - k;
            val = (val - 128) * .65f + 128;
            rgb[base + 2] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff
                    : (byte) (val + 0.5);
        }
        raster = Raster.createInterleavedRaster(new DataBufferByte(rgb, rgb.length), w, h, w * 3, 3, new int[]{0, 1, 2}, null);
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        ColorModel cm = new ComponentColorModel(cs, false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        return new BufferedImage(cm, (WritableRaster) raster, true, null);
    }

    public static String saveTempFile(MultipartFile savefile, String filePath) {
        try {
            return saveTempFile(savefile.getInputStream(), filePath);
        } catch (IOException e) {
            log.error("save temp file fail:msg:{},details:{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        }
        return "";
    }

    public static String saveTempFile(InputStream stream, String filePath) {

        File file = new File(filePath);
        File dic = new File(file.getParent());

        if (!dic.exists())
            dic.mkdirs();
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            boolean isSuccess = file.createNewFile();
            if (!isSuccess) {
                log.error("处理文件失败，无法创建临时文件，是否无权限~！" + filePath);
            }
            fileOutputStream = new FileOutputStream(file);
            inputStream = stream;
            byte[] by = new byte[1024];
            int size = -1;

            while ((size = inputStream.read(by)) != -1) {
                fileOutputStream.write(by, 0, by.length);
            }
        } catch (Exception e) {
            log.error(String.format("temporary file save error,error message %s,stack trace:%s,filePath:%s", e.getMessage(), ExceptionUtils.getStackTrace(e), filePath));
            throw new AppException(-40001, "param error");

        } finally {
            try {
                if (fileOutputStream != null)
                    fileOutputStream.close();
                if (inputStream != null)
                    inputStream.close();
            } catch (Exception e) {

            }
        }
        return filePath;
    }

    public static String getFileName(String path) {
        int index = path.lastIndexOf("/");
        if (index == -1) return path;//path.substring(prefixLength);
        return path.substring(index + 1);
    }

    //region 从url生成获取fileItem
    public static FileItem getFileItemFromUrl(String url) {
        InputStream inputStream = null;
        FileItem item = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse httpResponse = httpclient.execute(httpGet);
            inputStream = httpResponse.getEntity().getContent();

            //校验
            if (inputStream.available() > MAXSIZE) {
                throw new AppException(-46107, "图片上传错误，图片太大");
            }
            Header[] headers = httpResponse.getHeaders("Content-Type");
            if (headers == null || headers.length == 0 || !FILE_TYPE_MAP.keySet().contains(headers[0].getValue())) {
                throw new AppException(-46106, "图片上传错误，格式支持");
            }

            String contentType = headers[0].getValue();
            String suffix = FILE_TYPE_MAP.get(headers[0].getValue());
            item = new FileItemStream("uploader",
                    getFileName() + suffix,
                    contentType,
                    inputStream.available(),
                    inputStream);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return item;
    }

    private static String getFileName() {
        Random ranGen = new SecureRandom();
        ranGen.setSeed(UUID.randomUUID().hashCode());
        return new SimpleDateFormat("yyyyMMddhhmmssSSS").format(new Date()) + ranGen.nextInt(100000);
    }
    //endregion
}
