package com.perftest.vthreads.service;

import com.perftest.vthreads.model.VirtualThreadTraceResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VirtualThreadTraceServiceTests {

    @Test
    void tracesVirtualThreadLifecycleFromCreationToTermination() throws Exception {
        VirtualThreadTraceService service = new VirtualThreadTraceService(false);

        VirtualThreadTraceResponse response = service.traceLifecycle(2, 80, 10, 15);

        assertThat(response.taskCount()).isEqualTo(2);
        assertThat(response.events())
                .filteredOn(event -> event.phase().equals("created"))
                .hasSize(2)
                .allSatisfy(event -> assertThat(event.thread().state()).isEqualTo("NEW"));
        assertThat(response.events())
                .filteredOn(event -> event.phase().equals("started"))
                .hasSize(2)
                .allSatisfy(event -> assertThat(event.thread().virtual()).isTrue());
        assertThat(response.samples())
                .anySatisfy(sample -> assertThat(sample.threadStates()).containsValue("WAITING"));
        assertThat(response.samples())
                .anySatisfy(sample -> assertThat(sample.threadStates()).containsValue("TIMED_WAITING"));
        assertThat(response.events())
                .filteredOn(event -> event.phase().equals("terminated"))
                .hasSize(2)
                .allSatisfy(event -> assertThat(event.thread().state()).isEqualTo("TERMINATED"));
        assertThat(response.summary()).containsEntry("allTaskThreadsVirtual", true);
    }
}
