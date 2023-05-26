package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
public class ExecutorController {

    private final ExecutorService executorService;

    public ExecutorController(ExecutorService executorService) {
        this.executorService = executorService;
    }


    @GetMapping("/executor")
    public ResponseEntity<DemoHealth> demo() throws ExecutionException, InterruptedException {
        log.info("{}: controller endpoint start", Thread.currentThread().getName());
        this.executorService.doAsyncWork();
        log.info("{}: controller endpoint end", Thread.currentThread().getName());
        return ResponseEntity.ok(DemoHealth.builder().project("executor").time(Instant.now().toEpochMilli()).build());
    }

}
