package com.github.nenomm.marbles;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bottleneck {

  private static final Logger logger = LoggerFactory.getLogger(Bottleneck.class);

  @Test
  public void pour() {
    Observable.<Integer>just(1, 2, 3, 4, 5)
        .flatMapSingle(i -> Single.just(i * i))
        .subscribe(integer -> logger.info("{}", integer));

  }

  @Test
  public void maybePour() { // throws java.util.NoSuchElementException
    Maybe.<Integer>empty()
        .flatMapSingle(i -> Single.just(i * i))
        .doOnError(throwable -> logger.error("Hi there:", throwable))
        .subscribe(integer -> logger.info("{}", integer));
  }

  @Test
  public void maybePourSingle() {
    Maybe.<Integer>empty()
        .flatMapSingleElement(i -> Single.just(i * i))
        .subscribe(integer -> logger.info("{}", integer));
  }
}
