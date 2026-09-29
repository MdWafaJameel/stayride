package com.stayride.ride.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class TaskExecutorConfig {

    @Bean(name = "rideTaskExecutor")
    public ThreadPoolTaskExecutor rideTaskExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(3);
        executor.setQueueCapacity(10);
        executor.setMaxPoolSize(5);
        executor.setThreadNamePrefix("ride-worker-");

        executor.initialize();

        return executor;
    }
}