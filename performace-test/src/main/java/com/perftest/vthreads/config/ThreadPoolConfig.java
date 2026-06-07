package com.perftest.vthreads.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThreadPoolConfig {

    private static final Logger log = LoggerFactory.getLogger(ThreadPoolConfig.class);

    @Value("${spring.threads.virtual.enabled:false}")
    private boolean virtualThreadsEnabled;

    @PostConstruct
    public void logThreadMode() {
        if (virtualThreadsEnabled) {
            log.info("=======================================================");
            log.info("  Thread mode: VIRTUAL THREADS (Project Loom)");
            log.info("  spring.threads.virtual.enabled = true");
            log.info("  Tomcat will use virtual threads per request");
            log.info("=======================================================");
        } else {
            log.info("=======================================================");
            log.info("  Thread mode: PLATFORM THREADS (traditional)");
            log.info("  spring.threads.virtual.enabled = false");
            log.info("  Tomcat will use a bounded thread pool");
            log.info("=======================================================");
        }
    }

    public boolean isVirtualThreadsEnabled() {
        return virtualThreadsEnabled;
    }
}
