# `@Async` and `CompleableFuture`

Node is really great at this kind of async/non-blocking kind of programming model. Java and Spring have increasingly better support but the materials out in the wild are not quite there yet.

In this demo...

1. `@Async` annotation examples that show how a controller accepts a request, triggers "out of band" or async work, and the controller then responds with `200 OK` right after
2. For a non-annotation flavor version, a `CompletableFuture.supplyAsync`, which offers more flexibility

An interesting note about `@Async`, and its key usage quirk, is that it cannot be invokved by a method in the same class. So for example, you have a service, `OrderProductService`. This service has `placeOrder` which calls `@Async updatePromotions`. If `updatePromtions` is in the same class, well, it will appear as if the threads are always sticky. You'll have to separate out the sync work from the async work along a class division. This is a byproduct of how Spring annotations work internally. You can use the `CompletableFuture` approach as an alterantive but it is certainly an increase in localized complexity (but admittedly, needlessly splitting a monolithic class into two just for async activity certainly is not without complexity bloat either). 