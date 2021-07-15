package com.github.nenomm.marbles;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlatMapTest {

  private static final Logger logger = LoggerFactory.getLogger(FlatMapTest.class);

  @Test
  public void flatSingle() {
    Observable.just("A")
        .flatMap(s -> Observable
            .just("A"))
        .subscribe(logger::info);
  }

  @Test
  public void flatMulti() {
    Observable.just("A")
        .flatMap(s -> Observable
            .just("A", "B", "C"))
        .subscribe(logger::info);
  }

  @Test
  public void flatForever() {
    Observable.just("A")
        .repeat()
        .doOnNext(s -> logger.info("Using this one"))
        .flatMap(s -> Observable
            .just("A")
            .repeat())
        .take(10)
        .subscribe(logger::info);
  }

  @Test
  public void flatRepeated() {
    Observable.just("A")
        .repeat()
        .take(3)
        .doOnNext(s -> logger.info("Using this one"))
        .flatMap(s -> Observable.just("C"))
        .subscribe(logger::info);
  }

  @Test
  public void flatMapToAnotherType() {
    Maybe<Boolean> single = Maybe.just("A")
        .isEmpty()
        .toMaybe()
        .flatMap(aBoolean -> {
          if (aBoolean) {
            return Maybe.just(Boolean.TRUE);
          } else {
            return Maybe.just(Boolean.FALSE);
          }
        });
  }


}