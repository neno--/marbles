package com.github.nenomm.reactor;


import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.publisher.Mono;

public class MonoSubscription {

  private static final Logger logger = LoggerFactory.getLogger(MonoSubscription.class);


  @Test
  public void justCreateMono() {
    final Disposable disposable = Mono.just("Oh, hai")
        .doOnNext(s -> sleep(3000))
        .then(mono1())
        .subscribe();

    //disposable.dispose();

    Disposables.swap().replace(mono2().subscribe());

    logger.info("Done!");
  }

  private static Mono<String> mono1() {
    logger.info("In mono1");
    return Mono.just("mono1")
        .doOnNext(s -> sleep(3000))
        .doOnNext(s -> logger.info("Processing {}",s));
  }

  private static Mono<String> mono2() {
    logger.info("In mono2");
    return Mono.just("mono2")
        .doOnNext(s -> sleep(3000))
        .doOnNext(s -> logger.info("Processing {}",s));
  }

  private static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
