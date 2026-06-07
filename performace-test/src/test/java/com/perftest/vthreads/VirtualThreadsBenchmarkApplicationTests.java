package com.perftest.vthreads;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VirtualThreadsBenchmarkApplicationTests {

    @Test
    void contextLoads() {
        // Verifies that the Spring application context starts without errors.
        // Run with -Dspring.profiles.active=virtual-threads or platform-threads.
    }
}
