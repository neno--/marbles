package com.github.nenomm.reactor;


import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Errors {

  private static final Logger logger = LoggerFactory.getLogger(Errors.class);


  @Test
  public void itIsNotResumingOriginalFlux() {
    Flux.just(1, 2, 3, 4, 5, 6, 7)
        .flatMap(number -> {
          if (number == 4) {
            throw new RuntimeException("Boom!");
          }
          return Flux.just(number);
        })
        .onErrorResume(e -> Flux.just(100, 200, 300))
        .subscribe(integer -> logger.info("Arrived: {}", integer));

    logger.info("Done!");
  }

  @Test
  public void itIsResumingOriginalFlux() {
    Flux.just(1, 2, 3, 4, 5, 6, 7)
        .flatMap(number -> someService(number)
            .onErrorResume(e -> Mono.just(400)))
        .subscribe(integer -> logger.info("Arrived: {}", integer));

    logger.info("Done!");
  }

  private static Mono<Integer> someService(int i) {
    return Mono.just(i)
        .map(integer -> {
          if (i == 4) {
            throw new RuntimeException("Boom!");
          } else {
            return i;
          }
        });
  }
}
