package com.github.nenomm.reactor;


import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class DeferMono {

  private static final Logger logger = LoggerFactory.getLogger(DeferMono.class);


  @Test
  public void justCreateMono() {
    Mono.just("Oh, hai")
        .doOnNext(s -> sleep(3000))
        .then(mono1())
        .subscribe(MonoIntro.gimme.get());

    logger.info("Done!");
  }

  private static Mono<String> mono1() {
    logger.info("In mono1");
    return Mono.just("mono1")
        .doOnNext(s -> sleep(3000))
        .doOnNext(s -> logger.info("Processing {}",s));
  }

  @Test
  public void justCreateMono2() {
    Mono.just("Oh, hai")
        .doOnNext(s -> sleep(3000))
        .then(Mono.defer(() -> mono2()))
        .subscribe(MonoIntro.gimme.get());

    logger.info("Done!");
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
