package com.itisacat.basic.framework.core.util;

import com.itisacat.basic.framework.core.threads.ThreadUtil;
import org.slf4j.MDC;

/**
 * 获取本机IP Created by huangxin on 2015/6/2.
 */
public final class IpUtil {

    private IpUtil() {

    }

    public static String getIp() {
        return ThreadUtil.getOneLocalIP();
    }


    public static String getClientIp() {
        return MDC.get("clientIp");
    }

}
