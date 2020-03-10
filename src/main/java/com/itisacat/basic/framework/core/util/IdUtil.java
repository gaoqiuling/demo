package com.itisacat.basic.framework.core.util;

import java.util.UUID;

/**
 * 唯一序列号获取
 */
public class IdUtil {

    public static String getSeqID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    private IdUtil() {
        // to avoid construct instance
    }

}
