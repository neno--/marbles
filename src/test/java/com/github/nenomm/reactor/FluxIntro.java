package com.github.nenomm.reactor;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import static com.github.nenomm.marbles.SingleIntro.sleep;

public class FluxIntro {

    private static final Logger logger = LoggerFactory.getLogger(FluxIntro.class);

    Supplier<Subscriber<String>> gimme = () -> {
        return new Subscriber<String>() {

            @Override
            public void onSubscribe(Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
                logger.info("OBSERVER: Subscribed...");
            }

            @Override
            public void onNext(String s) {
                logger.info("OBSERVER: Success {}", s);
            }

            @Override
            public void onError(Throwable e) {
                logger.error("OBSERVER: KaBoom", e);
            }

            @Override
            public void onComplete() {
                logger.info("OBSERVER: Empty");
            }
        };
    };

    @Test
    public void justCreateFlux() {
        Flux<String> flux = Flux.just("A", "B", "C");

        flux.subscribe(gimme.get());

        logger.info("Done!");
    }

    @Test
    public void justCreateAnotherFlux() {
        Flux.just("red", "white", "blue")
                .map(String::toUpperCase)
                .subscribe(new Subscriber<String>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(String t) {
                        System.out.println(t);
                    }

                    @Override
                    public void onError(Throwable t) {
                    }

                    @Override
                    public void onComplete() {
                    }

                });

        logger.info("Done!");
    }

    @Test
    public void observableFlatMap() {
        Flux.just(3, 2, 1)
                .flatMap(i -> {
                    return Flux.just(i)
                            .publishOn(Schedulers.parallel())
                            .doOnNext(integer -> {
                                logger.info("going to sleep...");
                                sleep(i * 1000);
                                logger.info("wakey wakey");
                            });
                })
                .map(Objects::toString)
                .subscribe(gimme.get());
        logger.info("I am here");
        sleep(10000);
        logger.info("Done");
    }


    @Test
    public void flatMapAsync() throws ExecutionException, InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);

        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .flatMap(integer -> {
                    logger.info("Waiting for {}", integer);
                    return Mono.fromCallable(() -> {
                                logger.info("Sleeping on thread {} for {}", Thread.currentThread().getName(), integer);
                                Thread.sleep(1000);
                                return integer;
                            })
                            .subscribeOn(Schedulers.boundedElastic());
                })
                .doFinally(signalType -> {
                    latch.countDown();
                })
                .subscribe();

        latch.await();
    }

    @Test
    public void flatMapAsync2() throws ExecutionException, InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);

        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(integer -> {
                    logger.info("Waiting for {}", integer);
                    return Mono.fromCallable(() -> {
                        logger.info("Sleeping on thread {} for {}", Thread.currentThread().getName(), integer);
                        Thread.sleep(1000);
                        return integer;
                    });
                })
                .doFinally(signalType -> {
                    latch.countDown();
                })
                .subscribe();

        latch.await();
    }

    @Test
    public void flatMapAsync3() throws ExecutionException, InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);

        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .flatMap(integer -> {
                    logger.info("Waiting for {}", integer);
                    return Mono.fromCallable(() -> {
                                logger.info("Sleeping on thread {} for {}", Thread.currentThread().getName(), integer);
                                Thread.sleep(1000);
                                return integer;
                            });
                })
                .doFinally(signalType -> {
                    latch.countDown();
                })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();

        latch.await();
    }

    @Test
    public void flatMapAsync4() throws ExecutionException, InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);

        Flux.just(Mono.just(1)
                                .doOnNext(n -> System.out.println("Executing task #1..."))
                                .delayElement(Duration.ofMillis(5000))
                                .subscribeOn(Schedulers.boundedElastic()),
                        Mono.just(2)
                                .doOnNext(n -> System.out.println("Executing task #2..."))
                                .delayElement(Duration.ofMillis(2000))
                                .subscribeOn(Schedulers.boundedElastic()),
                        Mono.just(3)
                                .doOnNext(n -> System.out.println("Executing task #3..."))
                                .delayElement(Duration.ofMillis(1000))
                                .subscribeOn(Schedulers.boundedElastic()))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(integer -> {
                    logger.info("Waiting for {}", integer);
                    return integer;
                })
                .doFinally(signalType -> {
                    latch.countDown();
                })
                .subscribe(integer -> logger.info("Processed {}", integer));

        latch.await();
    }

}
