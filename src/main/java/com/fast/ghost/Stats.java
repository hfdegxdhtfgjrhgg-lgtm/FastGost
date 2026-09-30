package com.fast.ghost;

import java.util.concurrent.atomic.AtomicLong;

public final class Stats {

    private final AtomicLong hitsReceived = new AtomicLong(0);
    private final AtomicLong hitsCancelled = new AtomicLong(0);
    private final AtomicLong ghostHitsFixed = new AtomicLong(0);
    private final AtomicLong hitsLogged = new AtomicLong(0);

    public void incrementReceived() { hitsReceived.incrementAndGet(); }
    public void incrementCancelled() { hitsCancelled.incrementAndGet(); }
    public void incrementFixed() { ghostHitsFixed.incrementAndGet(); }
    public void incrementLogged() { hitsLogged.incrementAndGet(); }

    public long getHitsReceived() { return hitsReceived.get(); }
    public long getHitsCancelled() { return hitsCancelled.get(); }
    public long getGhostHitsFixed() { return ghostHitsFixed.get(); }
    public long getHitsLogged() { return hitsLogged.get(); }
}
