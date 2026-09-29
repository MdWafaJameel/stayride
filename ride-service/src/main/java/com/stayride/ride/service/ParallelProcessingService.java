package com.stayride.ride.service;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Service
public class ParallelProcessingService {

    private final ThreadPoolTaskExecutor executor;

    public ParallelProcessingService(ThreadPoolTaskExecutor executor) {
        this.executor = executor;
    }

    public void execute() {

        Future<String> task1 = executor.submit(() -> {
            System.out.println("Task 1 started: "
                    + Thread.currentThread().getName());

            Thread.sleep(1000);

            return "Task 1 completed";
        });

        Future<String> task2 = executor.submit(() -> {
            System.out.println("Task 2 started: "
                    + Thread.currentThread().getName());

            Thread.sleep(1000);

            return "Task 2 completed";
        });

        Future<String> task3 = executor.submit(() -> {
            System.out.println("Task 3 started: "
                    + Thread.currentThread().getName());

            Thread.sleep(1000);

            return "Task 3 completed";
        });

        try {
            System.out.println(task1.get());
            System.out.println(task2.get());
            System.out.println(task3.get());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}