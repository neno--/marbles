package com.github.nenomm.marbles;

import com.github.nenomm.CustomRepeat;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetryWhen {

  private static final Logger logger = LoggerFactory.getLogger(RetryWhen.class);

  @Test
  public void withTimer() throws InterruptedException {
    final AtomicInteger sourceInt = new AtomicInteger(-1);

    Single.just(0)
        .map(zero -> sourceInt.incrementAndGet())
        .doOnSubscribe(s -> logger.info("subscribing"))
        .doOnSuccess(i -> logger.info("Emitting {}", i))
        .map(i -> {
          if (i < 5) {
            logger.info("BOOM {}", i);
            throw new RuntimeException(String.format("boom %d", i));
          } else {
            logger.info("Everything is fine {}", i);
            return i;
          }
        })
        .retryWhen(errors -> {
          AtomicInteger counter = new AtomicInteger();
          return errors
              .doOnNext(ignored -> logger.info("In retryWhen for {}, value is {}", counter.get(), sourceInt.get()))
              .doOnNext(throwable -> logger.error("Retrying because of", throwable))
              .takeWhile(e -> counter.getAndIncrement() != 5)
              .flatMap(e -> {
                logger.info("delay retry by " + counter.get() + " second(s)");
                return Flowable.timer(counter.get(), TimeUnit.SECONDS);
              });
        })
        .doOnError(throwable -> logger.error("Has message: {}, message: {}", throwable.getMessage() != null, throwable.getMessage()))
        .subscribe(integer -> logger.info("Done, result is: {}", integer));

    Thread.sleep(60 * 1000);
  }

  @Test
  public void withPendulum() throws InterruptedException {
    Single.just(23)
        .doOnSubscribe(s -> logger.info("subscribing"))
        .doOnSuccess(i -> logger.info("Emitting {}", i))
        .map(i -> {
          throw new IllegalArgumentException(String.format("boom %d", i));
        })
        .repeatWhen(new CustomRepeat(5))
        .firstElement()
        .onErrorResumeNext(error -> {
          if (error instanceof RuntimeException) {
            return Maybe.empty();
          }
          return Maybe.error(error);
        })
        .subscribe();

    Thread.sleep(60 * 1000);
  }
}
