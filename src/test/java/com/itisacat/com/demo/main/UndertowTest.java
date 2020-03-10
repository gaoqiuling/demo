package com.itisacat.com.demo.main;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.resource.PathResourceManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

@SpringBootTest
@RunWith(SpringRunner.class)

public class UndertowTest {
    @Test
    public void testExcel() {
        File file = new File("/");
        Undertow server = Undertow.builder().addHttpListener(8090, "localhost")
                .setHandler(Handlers.resource(new PathResourceManager(file.toPath(), 100))
                        .setDirectoryListingEnabled(true))
                .build();
        server.start();
        try {
            Thread.sleep(500000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
