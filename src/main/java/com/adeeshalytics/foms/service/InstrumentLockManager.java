package com.adeeshalytics.foms.service;

import org.springframework.stereotype.Component;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/** Single-instance coordination only; database state remains authoritative. */
@Component
public class InstrumentLockManager {
    private final ConcurrentHashMap<UUID, ReentrantLock> locks = new ConcurrentHashMap<>();
    public ReentrantLock forInstrument(UUID instrumentId) { return locks.computeIfAbsent(instrumentId, ignored -> new ReentrantLock(true)); }
}
