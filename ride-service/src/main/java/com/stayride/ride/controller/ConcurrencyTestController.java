package com.stayride.ride.controller;

import com.stayride.ride.service.CompletableFutureService;
import com.stayride.ride.service.ParallelProcessingService;
import com.stayride.ride.service.RaceConditionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/concurrency")
@RequiredArgsConstructor
public class ConcurrencyTestController {

    private final RaceConditionService raceConditionService;
    private final CompletableFutureService completableFutureService;
    private final ParallelProcessingService parallelProcessingService;

    @PostMapping("/race")
    public String testRaceCondition() {

        Thread thread1 = new Thread(
                raceConditionService::bookDriver,
                "request-1"
        );

        Thread thread2 = new Thread(
                raceConditionService::bookDriver,
                "request-2"
        );

        thread1.start();
        thread2.start();

        return "Race condition test started";
    }

    @PostMapping("/ride-estimate")
    public CompletableFuture<String> rideEstimate() {
        return completableFutureService.getRideEstimate();
    }

    @PostMapping("/completable")
    public CompletableFuture<String> testCompletableFuture() {

        return completableFutureService.executeTask();
    }

    @PostMapping("/parallel")
    public String testParallelProcessing() {

        long start = System.currentTimeMillis();

        parallelProcessingService.execute();

        long end = System.currentTimeMillis();

        return "Execution time: " + (end - start) + " ms";
    }
}