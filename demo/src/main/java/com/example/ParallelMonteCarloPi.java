package com.example;


import java.util.concurrent.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class ParallelMonteCarloPi {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        long totalIterations = 1_000_000_000L; 
        int numThreads = Runtime.getRuntime().availableProcessors(); 
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        long iterationsPerThread = totalIterations / numThreads;
        System.out.println("Кількість потоків: " + numThreads);
        System.out.println("Ітерацій на потік: " + iterationsPerThread);

        Callable<Long> task = () -> {
            Random rand = new Random();
            long count = 0;
            for (long i = 0; i < iterationsPerThread; i++) {
                double x = rand.nextDouble();
                double y = rand.nextDouble();
                if (x * x + y * y <= 1) {
                    count++;
                }
            }
            return count;
        };

        List<Future<Long>> futures = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numThreads; i++) {
            futures.add(executor.submit(task));
        }

        long totalCount = 0;
        for (Future<Long> future : futures) {
            totalCount += future.get();
        }

        executor.shutdown();

        double pi = 4.0 * totalCount / totalIterations;
        long endTime = System.currentTimeMillis();

        System.out.printf("Приблизне значення числа Пі: %.6f%n", pi);
        System.out.printf("Час виконання: %.2f сек%n", (endTime - startTime) / 1000.0);
    }
}
