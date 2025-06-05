package com.placeholder.placeholder.util;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ThreadPoolConfig {

    /**
     * Creates a thread pool for executing mathematical evaluations.
     * <p>
     * The pool is configured with a core size of 10 threads, a maximum size of 20 threads,
     * and a keep-alive time of 60 seconds for idle threads. It uses a bounded queue to hold
     * tasks before they are executed.
     *
     * @return an ExecutorService instance configured for math evaluations
     */
    @Bean(destroyMethod = "shutdown")
    public ExecutorService mathThreadPool() {
        int cpuCores = Runtime.getRuntime().availableProcessors();

        int corePoolSize = cpuCores;
        int maxPoolSize = cpuCores;
        long keepAliveTime = 60L;
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(100);

        ThreadFactory namedThreadFactory = new ThreadFactory() {
            private final AtomicInteger count = new AtomicInteger(1);
            @Override
            public Thread newThread(@NotNull Runnable r) {
                Thread t = new Thread(r);
                t.setName("math-pool-thread-" + count.getAndIncrement());
                return t;
            }
        };

        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                queue,
                namedThreadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }


}
