package com.placeholder.placeholder.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync // enables @Async annotation support
public class AsyncConfig {

    /**
     * Configures a ThreadPoolTaskExecutor for handling asynchronous tasks related to mathematical evaluations.
     * <p>
     * This executor is used to process math expressions in a separate thread pool, allowing for concurrent evaluations
     * without blocking the main application thread.
     * </p>
     *
     * @return a configured ThreadPoolTaskExecutor instance
     */
    @Bean("mathExecutor")
    public Executor mathExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("MathExec-");
        executor.initialize();
        return executor;
    }
}
