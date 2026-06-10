package com.studycs.webfluxdemo.config

import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import reactor.blockhound.BlockHound

@Configuration(proxyBeanMethods = false)
@Profile("blockhound")
class BlockHoundConfig {
    @PostConstruct
    fun installBlockHound() {
        // Optional experiment:
        // Run with --spring.profiles.active=blockhound and then call /demo/blocking/sleep.
        // BlockHound instruments Reactor threads and reports blocking calls such as Thread.sleep.
        BlockHound.builder()
            .allowBlockingCallsInside("java.io.PrintStream", "write")
            .install()
    }
}

