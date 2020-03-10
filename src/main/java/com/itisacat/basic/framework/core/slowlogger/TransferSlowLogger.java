package com.itisacat.basic.framework.core.slowlogger;

import com.itisacat.basic.framework.consts.SysRestConsts;
import com.itisacat.basic.framework.core.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.MDC;

import java.util.Date;


public class TransferSlowLogger {
    
    protected TransferSlowLogger(){}

    protected static void markSlowLogger(long requestTime, long timeout, String path, Logger log) {
        try {
            if (requestTime > 0l) {
                long receiveTimestamp = DateUtil.getTimestampInMillis();
                long timeDifference = Math.abs(receiveTimestamp - requestTime);
                if (timeDifference >= timeout) {
                    log.warn("hjframeworkSlowLogHttp transfer_time_out(ms):{}; sent_at:{}; received_at:{}; client_ip:{}; server_ip:{}; reqeust_url:{};",
                            timeDifference, dataToString(requestTime), dataToString(receiveTimestamp),
                            MDC.get(SysRestConsts.CLINET_IP), MDC.get(SysRestConsts.SERVER_IP), path);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private static String dataToString(long timestamp) {
        return DateUtil.toDateString(new Date(timestamp), DateUtil.DEFAULT_DATEDETAIL_PATTERN);
    }
}
