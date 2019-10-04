package eu.mikroskeem.jvmhiccup;

import java.util.concurrent.TimeUnit;
import java.util.function.LongConsumer;

/**
 * @author Mark Vainomaa
 */
public final class HiccupMeterThread extends Thread {
    /** A value what stands for hiccup meter thread being terminated */
    public static final long TERMINATED_VALUE = -1;

    private static boolean allocateObjects = true;
    public volatile Long lastSleepTimeObj; // public volatile to make sure allocs are not optimized away

    private final LongConsumer callback;
    private final int resolutionMs;

    private volatile long lastHiccupTime = TERMINATED_VALUE;
    private volatile boolean doRun = true;

    public HiccupMeterThread(LongConsumer callback) {
        this(callback, 10);
    }

    public HiccupMeterThread(LongConsumer callback, int resolutionMs) {
        super("JVM hiccup meter thread");
        this.callback = callback;
        this.resolutionMs = resolutionMs;
        setDaemon(true);
    }

    @Override
    @SuppressWarnings({"deprecation", "UnnecessaryBoxing"})
    public void run() {
        final long resolutionNsec = resolutionMs * 1000L * 1000L;
        try {
            long shortestObservedDeltaTimeNsec = Long.MAX_VALUE;
            long timeBeforeMeasurement = Long.MAX_VALUE;
            while (doRun) {
                TimeUnit.NANOSECONDS.sleep(resolutionNsec);
                if (allocateObjects) {
                    // Allocate an object to make sure potential allocation stalls are measured.
                    lastSleepTimeObj = new Long(timeBeforeMeasurement);
                }
                final long timeAfterMeasurement = System.nanoTime();
                final long deltaTimeNsec = timeAfterMeasurement - timeBeforeMeasurement;
                timeBeforeMeasurement = timeAfterMeasurement;

                if (deltaTimeNsec < 0) {
                    // On the very first iteration (which will not time the loop in it's entirety)
                    // the delta will be negative, and we'll skip recording.
                    continue;
                }

                if (deltaTimeNsec < shortestObservedDeltaTimeNsec) {
                    shortestObservedDeltaTimeNsec = deltaTimeNsec;
                }

                long hiccupTimeNsec = deltaTimeNsec - shortestObservedDeltaTimeNsec;

                callback.accept(lastHiccupTime = hiccupTimeNsec);
            }
        } catch (InterruptedException ignored) {}
        callback.accept(lastHiccupTime = TERMINATED_VALUE);
    }

    public void terminate() {
        doRun = false;
    }

    public long getLastHiccupTime() {
        return lastHiccupTime;
    }
}
