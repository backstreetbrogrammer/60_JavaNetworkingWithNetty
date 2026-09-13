package com.backstreetbrogrammer.part1.http.httpclient;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Accumulator {

    private final MyWebClient webClient;

    public Accumulator() {
        this.webClient = new MyWebClient();
    }

    public List<String> sendTasksToWorkers(final List<String> workersAddresses, final List<String> tasks) {
        final CompletableFuture<String>[] futures = new CompletableFuture[workersAddresses.size()];

        for (int i = 0; i < workersAddresses.size(); i++) {
            final String workerAddress = workersAddresses.get(i);
            final String task = tasks.get(i);

            final byte[] requestPayload = task.getBytes();
            futures[i] = webClient.sendTask(workerAddress, requestPayload);
        }

        return Stream.of(futures)
                     .map(CompletableFuture::join)
                     .collect(Collectors.toList());
    }
}
