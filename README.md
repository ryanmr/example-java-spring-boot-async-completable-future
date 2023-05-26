# `@Async` and `CompleableFuture`

Node is really great at this kind of async/non-blocking kind of programming model. Java and Spring have increasingly better support but the materials out in the wild are not quite there yet.

In this demo...

1. `@Async` annotation examples that show how a controller accepts a request, triggers "out of band" or async work, and the controller then responds with `200 OK` right after
2. For a non-annotation flavor version, a `CompletableFuture.supplyAsync`, which offers more flexibility

An interesting note about `@Async`, and its key usage quirk, is that it cannot be invokved by a method in the same class. So for example, you have a service, `OrderProductService`. This service has `placeOrder` which calls `@Async updatePromotions`. If `updatePromtions` is in the same class, well, it will appear as if the threads are always sticky. You'll have to separate out the sync work from the async work along a class division. This is a byproduct of how Spring annotations work internally. You can use the `CompletableFuture` approach as an alterantive but it is certainly an increase in localized complexity (but admittedly, needlessly splitting a monolithic class into two just for async activity certainly is not without complexity bloat either). 


Here's a sample of the logs from the `CompletableFuture` approach:

```
2023-05-25T22:37:02.159-05:00  INFO 93298 --- [nio-8080-exec-1] com.example.demo.ExecutorController      : http-nio-8080-exec-1: controller endpoint start
2023-05-25T22:37:02.160-05:00  INFO 93298 --- [nio-8080-exec-1] com.example.demo.ExecutorService         : http-nio-8080-exec-1: handle do async work start
2023-05-25T22:37:02.161-05:00  INFO 93298 --- [nio-8080-exec-1] com.example.demo.ExecutorService         : http-nio-8080-exec-1: starting future 1
2023-05-25T22:37:02.161-05:00  INFO 93298 --- [nio-8080-exec-1] com.example.demo.ExecutorService         : http-nio-8080-exec-1: starting future 2
2023-05-25T22:37:02.161-05:00  INFO 93298 --- [nio-8080-exec-1] com.example.demo.ExecutorService         : http-nio-8080-exec-1: starting future 3
2023-05-25T22:37:02.161-05:00  INFO 93298 --- [nio-8080-exec-1] com.example.demo.ExecutorService         : http-nio-8080-exec-1: starting future 4
2023-05-25T22:37:02.161-05:00  INFO 93298 --- [nio-8080-exec-1] com.example.demo.ExecutorService         : http-nio-8080-exec-1: starting future 5
2023-05-25T22:37:02.161-05:00  INFO 93298 --- [nio-8080-exec-1] com.example.demo.ExecutorService         : http-nio-8080-exec-1: starting future 6
2023-05-25T22:37:02.162-05:00  INFO 93298 --- [onPool-worker-1] com.example.demo.ExecutorService         : ForkJoinPool.commonPool-worker-1: run async start for future 1
2023-05-25T22:37:02.162-05:00  INFO 93298 --- [nio-8080-exec-1] com.example.demo.ExecutorService         : http-nio-8080-exec-1: starting future 7
2023-05-25T22:37:02.162-05:00  INFO 93298 --- [nio-8080-exec-1] com.example.demo.ExecutorService         : http-nio-8080-exec-1: starting future 8
2023-05-25T22:37:02.162-05:00  INFO 93298 --- [nio-8080-exec-1] com.example.demo.ExecutorService         : http-nio-8080-exec-1: starting future 9
2023-05-25T22:37:02.162-05:00  INFO 93298 --- [nio-8080-exec-1] com.example.demo.ExecutorService         : http-nio-8080-exec-1: starting future 10
2023-05-25T22:37:02.162-05:00  INFO 93298 --- [nio-8080-exec-1] com.example.demo.ExecutorService         : http-nio-8080-exec-1: futures: [java.util.concurrent.CompletableFuture@1503a636[Not completed], java.util.concurrent.CompletableFuture@2bbb27b8[Not completed], java.util.concurrent.CompletableFuture@7182d72f[Not completed], java.util.concurrent.CompletableFuture@61d7beb1[Not completed], java.util.concurrent.CompletableFuture@696d96ab[Not completed], java.util.concurrent.CompletableFuture@1b19ead9[Not completed], java.util.concurrent.CompletableFuture@7cc57d3a[Not completed], java.util.concurrent.CompletableFuture@203b7625[Not completed], java.util.concurrent.CompletableFuture@25666447[Not completed], java.util.concurrent.CompletableFuture@71fd1d10[Not completed]]
2023-05-25T22:37:02.162-05:00  INFO 93298 --- [onPool-worker-2] com.example.demo.ExecutorService         : ForkJoinPool.commonPool-worker-2: run async start for future 2
2023-05-25T22:37:02.162-05:00  INFO 93298 --- [nio-8080-exec-1] com.example.demo.ExecutorService         : http-nio-8080-exec-1: handle do async work end
2023-05-25T22:37:02.162-05:00  INFO 93298 --- [nio-8080-exec-1] com.example.demo.ExecutorController      : http-nio-8080-exec-1: controller endpoint end
2023-05-25T22:37:02.162-05:00  INFO 93298 --- [onPool-worker-3] com.example.demo.ExecutorService         : ForkJoinPool.commonPool-worker-3: run async start for future 3
2023-05-25T22:37:02.162-05:00  INFO 93298 --- [onPool-worker-4] com.example.demo.ExecutorService         : ForkJoinPool.commonPool-worker-4: run async start for future 4
2023-05-25T22:37:02.162-05:00  INFO 93298 --- [onPool-worker-5] com.example.demo.ExecutorService         : ForkJoinPool.commonPool-worker-5: run async start for future 5
2023-05-25T22:37:02.162-05:00  INFO 93298 --- [onPool-worker-6] com.example.demo.ExecutorService         : ForkJoinPool.commonPool-worker-6: run async start for future 6
2023-05-25T22:37:02.162-05:00  INFO 93298 --- [onPool-worker-7] com.example.demo.ExecutorService         : ForkJoinPool.commonPool-worker-7: run async start for future 7
2023-05-25T22:37:02.529-05:00  INFO 93298 --- [onPool-worker-1] com.example.demo.ExecutorService         : ForkJoinPool.commonPool-worker-1: run async end for future 1
2023-05-25T22:37:02.529-05:00  INFO 93298 --- [onPool-worker-1] com.example.demo.ExecutorService         : ForkJoinPool.commonPool-worker-1: run async start for future 8
2023-05-25T22:37:02.531-05:00  INFO 93298 --- [onPool-worker-6] com.example.demo.ExecutorService         : ForkJoinPool.commonPool-worker-6: run async end for future 6
2023-05-25T22:37:02.531-05:00  INFO 93298 --- [onPool-worker-6] com.example.demo.ExecutorService         : ForkJoinPool.commonPool-worker-6: run async start for future 9
2023-05-25T22:37:02.532-05:00  INFO 93298 --- [onPool-worker-5] com.example.demo.ExecutorService         : ForkJoinPool.commonPool-worker-5: run async end for future 5
2023-05-25T22:37:02.532-05:00  INFO 93298 --- [onPool-worker-5] com.example.demo.ExecutorService         : ForkJoinPool.commonPool-worker-5: run async start for future 10
2023-05-25T22:37:02.538-05:00  INFO 93298 --- [onPool-worker-3] com.example.demo.ExecutorService         : ForkJoinPool.commonPool-worker-3: run async end for future 3
2023-05-25T22:37:02.544-05:00  INFO 93298 --- [onPool-worker-2] com.example.demo.ExecutorService         : ForkJoinPool.commonPool-worker-2: run async end for future 2
2023-05-25T22:37:02.547-05:00  INFO 93298 --- [onPool-worker-7] com.example.demo.ExecutorService         : ForkJoinPool.commonPool-worker-7: run async end for future 7
2023-05-25T22:37:02.590-05:00  INFO 93298 --- [onPool-worker-4] com.example.demo.ExecutorService         : ForkJoinPool.commonPool-worker-4: run async end for future 4
2023-05-25T22:37:02.653-05:00  INFO 93298 --- [onPool-worker-6] com.example.demo.ExecutorService         : ForkJoinPool.commonPool-worker-6: run async end for future 9
2023-05-25T22:37:02.662-05:00  INFO 93298 --- [onPool-worker-1] com.example.demo.ExecutorService         : ForkJoinPool.commonPool-worker-1: run async end for future 8
2023-05-25T22:37:02.668-05:00  INFO 93298 --- [onPool-worker-5] com.example.demo.ExecutorService         : ForkJoinPool.commonPool-worker-5: run async end for future 10
```

These logs should show that it is truly non-deterministic completion (due to network latencies, and other things). But it should also show that the "handle do async work" start and end is contained within the controller but the work that method originally started is completed well outside of its lifetime.