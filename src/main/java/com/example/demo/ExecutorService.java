package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Service
@Slf4j
public class ExecutorService {

    private final RestTemplate restTemplate;

    public ExecutorService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void doAsyncWork() {
        log.info("{}: handle do async work start", Thread.currentThread().getName());

        var futures = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).map(val -> {
            log.info("{}: starting future {}", Thread.currentThread().getName(), val);
            return CompletableFuture.supplyAsync(() -> {
                log.info("{}: run async start for future {}", Thread.currentThread().getName(), val);
                var response = this.restTemplate.getForObject("https://ryanrampersad.com/", String.class);
                log.info("{}: run async end for future {}", Thread.currentThread().getName(), val);
                return response;
            });
        }).toList();

        log.info("{}: futures: {}", Thread.currentThread().getName(), futures);

        log.info("{}: handle do async work end", Thread.currentThread().getName());
    }
}
