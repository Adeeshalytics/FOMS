package com.adeeshalytics.foms.service;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.assertThat;

class InstrumentLockManagerTest {
    @Test
    void serializesTasksForTheSameInstrument() throws Exception {
        InstrumentLockManager locks = new InstrumentLockManager();
        UUID instrument = UUID.randomUUID();
        ExecutorService pool = Executors.newFixedThreadPool(8);
        CountDownLatch ready = new CountDownLatch(8), start = new CountDownLatch(1), done = new CountDownLatch(8);
        java.util.concurrent.atomic.AtomicInteger active = new java.util.concurrent.atomic.AtomicInteger();
        java.util.concurrent.atomic.AtomicInteger peak = new java.util.concurrent.atomic.AtomicInteger();
        for (int i = 0; i < 8; i++) pool.submit(() -> { ready.countDown(); start.await(); var lock = locks.forInstrument(instrument); lock.lock(); try { peak.accumulateAndGet(active.incrementAndGet(), Math::max); Thread.sleep(10); active.decrementAndGet(); } finally { lock.unlock(); done.countDown(); } return null; });
        assertThat(ready.await(2, TimeUnit.SECONDS)).isTrue(); start.countDown(); assertThat(done.await(3, TimeUnit.SECONDS)).isTrue(); pool.shutdownNow();
        assertThat(peak.get()).isOne();
    }
}
