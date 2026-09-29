package com.stayride.ride.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class CompletableFutureService {

    private final ThreadPoolTaskExecutor executor;

    public CompletableFutureService(
            @Qualifier("rideTaskExecutor")
            ThreadPoolTaskExecutor executor) {
        this.executor = executor;
    }

    public CompletableFuture<String> getRideEstimate() {

        CompletableFuture<Double> fareFuture =
                CompletableFuture.supplyAsync(() -> {
                    System.out.println(
                            "Calculating fare on: "
                                    + Thread.currentThread().getName()
                    );

                    return 450.0;
                }, executor);

        CompletableFuture<Integer> etaFuture =
                CompletableFuture.supplyAsync(() -> {
                    System.out.println(
                            "Calculating ETA on: "
                                    + Thread.currentThread().getName()
                    );

                    return 12;
                }, executor);

        CompletableFuture<Double> distanceFuture =
                CompletableFuture.supplyAsync(() -> {
                    System.out.println(
                            "Calculating distance on: "
                                    + Thread.currentThread().getName()
                    );

                    return 8.5;
                }, executor);

        return CompletableFuture.allOf(
                fareFuture,
                etaFuture,
                distanceFuture
        ).thenApply(v -> {

            Double fare = fareFuture.join();
            Integer eta = etaFuture.join();
            Double distance = distanceFuture.join();

            return "Fare: ₹" + fare
                    + ", ETA: " + eta + " mins"
                    + ", Distance: " + distance + " km";
        });
    }

    public CompletableFuture<String> executeTask() {

        CompletableFuture<String> task1 =
                CompletableFuture.supplyAsync(() -> {
                    System.out.println("Task 1: "
                            + Thread.currentThread().getName());

                    sleep();

                    return "Task 1 result";
                }, executor);

        CompletableFuture<String> task2 =
                CompletableFuture.supplyAsync(() -> {
                    System.out.println("Task 2: "
                            + Thread.currentThread().getName());

                    sleep();

                    return "Task 2 result";
                }, executor);

        CompletableFuture<String> task3 =
                CompletableFuture.supplyAsync(() -> {
                    System.out.println("Task 3: "
                            + Thread.currentThread().getName());

                    sleep();

                    return "Task 3 result";
                }, executor);

        return CompletableFuture.allOf(task1, task2, task3)
                .thenApply(v ->
                        task1.join() + ", "
                                + task2.join() + ", "
                                + task3.join()
                );
    }

    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}