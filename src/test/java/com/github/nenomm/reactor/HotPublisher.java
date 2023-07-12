package com.github.nenomm.reactor;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class HotPublisher {
    private static final Logger logger = LoggerFactory.getLogger(HotPublisher.class);

    @Test
    void hotFromFlux() {
        final AtomicReference<SynchronousSink<Integer>> sinkRef = new AtomicReference<>();
        final AtomicBoolean useGenerator = new AtomicBoolean(true);

        Flux<Integer> flux = Flux.<Integer>generate(integerSynchronousSink -> {
                    if (useGenerator.get()) {
                        sinkRef.set(integerSynchronousSink);
                        integerSynchronousSink.next(1);
                        useGenerator.set(false);
                    }
                })
                .take(5);
        //.delayElements(Duration.ofMillis(250))
        //.take(5);

        Flux<Integer> first = flux.doOnNext(integer -> System.out.println("From first " + integer + " from thread " + Thread.currentThread().getName()));
        first.subscribe(integer -> System.out.println("Consuming first " + integer + " from thread " + Thread.currentThread().getName()));

        assert (sinkRef.get() != null);

        sinkRef.get().next(10);

        //sinkRef.get().next(2);
        Flux<Integer> second = flux.doOnNext(integer -> System.out.println("From second " + integer + " from thread " + Thread.currentThread().getName()));
        //sinkRef.get().next(3);
        //sinkRef.get().next(4);
        second.subscribe(integer -> System.out.println("Consuming second " + integer + " from thread " + Thread.currentThread().getName()));
        //sinkRef.get().next(5);

    }
}
