package com.github.nenomm.reactor;


import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class Errors {

  private static final Logger logger = LoggerFactory.getLogger(Errors.class);


  @Test
  public void itIsNotResumingOriginalFlux() {
    Flux.just(1, 2, 3, 4, 5, 6, 7)
        .flatMap(number -> someService(number))
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
            }
        );
  }

  @Test
  public void tryAgain() throws InterruptedException {
    Mono.defer(() -> {
          logger.info("Doin' it");
          return Mono.just(4);
        })
        .flatMap(number -> someService(number))
        .retryWhen(Retry.backoff(5, Duration.ofMillis(100)).filter(e -> e instanceof RuntimeException))
        .subscribe(integer -> logger.info("Arrived: {}", integer));

    Thread.sleep(5000);
    logger.info("Done!");
  }

  @Test
  public void tryNestedAgain() throws InterruptedException {
    Mono.defer(() -> {
          int state = 0;
          logger.info("Doin' it");
          return Mono.just(state++);
        })
        .flatMap(number -> Mono.just(number)
            .doOnNext(integer -> logger.info("I am here!"))
            .filter(thisNumber -> thisNumber > 3)
            .switchIfEmpty(
                Mono.fromRunnable(() -> logger.info("Now I am here!"))
                    .then(Mono.just(33))
                    .doOnNext(integer -> logger.info("What about me?"))
                    .then(Mono.error(new RuntimeException("EMPTY"))))
            .retryWhen(Retry.backoff(5, Duration.ofMillis(100)).filter(e -> e instanceof RuntimeException)))
        .subscribe(integer -> logger.info("Arrived: {}", integer));

    Thread.sleep(5000);
    logger.info("Done!");
  }


}
