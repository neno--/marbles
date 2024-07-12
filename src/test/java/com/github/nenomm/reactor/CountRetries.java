package com.github.nenomm.reactor;


import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class CountRetries {

  private final int NUMBER_OF_RETRIES = 5;

  private static final Logger logger = LoggerFactory.getLogger(CountRetries.class);


  @Test
  public void howManyRetries() {
    final AtomicInteger counter = new AtomicInteger(20);

    Mono.fromCallable(() -> {
      final int number = counter.getAndDecrement();
          if (number == 0) {
            logger.info("Returning the number");
            return 0;
          } else {
            logger.info("Number is {}, It's gonna blow", number);
            throw new RuntimeException("KaBoom");
          }
        })
        .doOnError(throwable -> logger.error("oh noes, error happened"))
        .retryWhen(Retry.backoff(NUMBER_OF_RETRIES, Duration.ofMillis(1000))
            .filter(e -> e instanceof RuntimeException)
            .doBeforeRetryAsync(retrySignal ->
                Mono.fromRunnable(() -> logger.info("   Before retry number: {}", retrySignal.totalRetriesInARow())))
            .doAfterRetryAsync(retrySignal -> Mono.fromRunnable(() -> logger.info("   After retry number: {}", retrySignal.totalRetriesInARow())))
        )
        .doOnNext(o -> logger.info("Doing other stuff"))
        .block();

    logger.info("Done!");
  }
}
