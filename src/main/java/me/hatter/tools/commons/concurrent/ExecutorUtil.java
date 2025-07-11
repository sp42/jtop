package me.hatter.tools.commons.concurrent;

import java.lang.management.ManagementFactory;
import java.util.concurrent.*;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

public class ExecutorUtil {

    public static ExecutorService getCPULikeExecutor(Integer count) {
        int processorCount = (count != null) ? count : ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
        ExecutorService executor;

        if (processorCount <= 1) {
            executor = new OneThreadExecutor();
        } else {
            executor = new ThreadPoolExecutor(processorCount - 1, processorCount - 1, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(processorCount),
                    Executors.defaultThreadFactory(), new CallerRunsPolicy());
        }

        return executor;
    }
}
