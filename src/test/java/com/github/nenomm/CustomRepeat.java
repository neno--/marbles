package com.github.nenomm;

import com.github.nenomm.marbles.RetryWhen;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomRepeat implements Function<Flowable<?>, Publisher<?>> {
  private static final Logger logger = LoggerFactory.getLogger(CustomRepeat.class);

  private final int retries;
  private int attemptsCount;

  public CustomRepeat(int retries) {
    this.retries = retries;
  }

  @Override
  public Publisher<?> apply(final Flowable<?> attempts) throws Exception {
    return attempts
        .doOnNext(o -> logger.info("Next one..."))
        .flatMap((Function<Object, Publisher<?>>) value -> {
          if (attemptsCount < retries) {
            attemptsCount = attemptsCount + 1;
            logger.info("Incrementing count: {}", attemptsCount);
            // When this Observable calls onNext, the original
            // Observable will be retried (i.e. re-subscribed).
            return Flowable.just(0);
            // return Flowable.timer(0, TimeUnit.MILLISECONDS);
          }
          // Max retries hit. Just pass the error along.
          return Flowable.error(new RuntimeException("Failed to retry"));
        });
  }
}
