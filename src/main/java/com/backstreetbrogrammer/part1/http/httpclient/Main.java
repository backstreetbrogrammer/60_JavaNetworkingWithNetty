package com.backstreetbrogrammer.part1.http.httpclient;

import java.util.List;

public class Main {

    private static final String WORKER_ADDRESS_1 = "http://localhost:8081/task";
    private static final String WORKER_ADDRESS_2 = "http://localhost:8082/task";

    public static void main(final String[] args) {
        final Accumulator accumulator = new Accumulator();
        final String task1 = "10,200";
        final String task2 = "123456789,100000000000000,700000002342343";

        final List<String> results = accumulator.sendTasksToWorkers(List.of(WORKER_ADDRESS_1, WORKER_ADDRESS_2),
                                                                    List.of(task1, task2));

        for (final String result : results) {
            System.out.println(result);
        }
    }
}
