package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class AsyncService {

    private final RestTemplate restTemplate;

    public AsyncService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async
    public CompletableFuture<String> handleRequestAsync() {
        log.info("{}: handle request async start", Thread.currentThread().getName());
        var obj = this.restTemplate.getForObject("https://ryanrampersad.com/", String.class);
        log.info("{}: handle request async end", Thread.currentThread().getName());
        return CompletableFuture.completedFuture(obj);
    }

}
