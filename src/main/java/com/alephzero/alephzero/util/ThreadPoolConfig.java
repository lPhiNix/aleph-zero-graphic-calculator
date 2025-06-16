package com.alephzero.alephzero.util;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ThreadPoolConfig {
    /**
     * The number of threads to keep in the pool, even if they are idle.
     * Set to the number of available processors.
     */
    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();

    /**
     * The maximum number of threads allowed in the pool.
     * Set to the number of available processors.
     */
    private static final int MAXIMUM_POOL_SIZE = Runtime.getRuntime().availableProcessors();

    /**
     * The maximum time that excess idle threads will wait for new tasks before terminating.
     * Measured in seconds.
     */
    private static final long KEEP_ALIVE_TIME = 60L;

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

        // thread pool executor.
        return new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                queue,
                namedThreadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }


}
