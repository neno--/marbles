package com.github.nenomm.reactor;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class Callbacks {
  private static final Logger logger = LoggerFactory.getLogger(Callbacks.class);
  @Test
  public void shouldCallOnError() {
    Flux<Integer> f = Flux.just(1,2,3)
        .doOnError(throwable -> logger.error("There was an error"));

    StepVerifier.create(f.concatWith(Flux.error(new RuntimeException())))
        .expectNextCount(3)
        .verifyError();
  }
}
