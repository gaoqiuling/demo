package com.itisacat.basic.framework.core.httpclient.logger;

import java.util.concurrent.atomic.AtomicLong;

public final class RestLoggingId {

    private RestLoggingId() {

    }

    private static final AtomicLong ID = new AtomicLong(0);

    public static long incrementAndGet() {
        return ID.incrementAndGet();
    }
}
