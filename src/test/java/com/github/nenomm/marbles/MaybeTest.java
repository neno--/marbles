package com.github.nenomm.marbles;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaybeTest {

  private static final Logger logger = LoggerFactory.getLogger(MaybeTest.class);

  @Test
  public void testKaBoom() {
    Maybe.just(null)
        .subscribe(o -> logger.info("Null: {}", o));
  }

  @Test
  public void testEmpty() {
    Maybe.fromCallable(() -> null)
        .subscribe(o -> logger.info("Null: {}", o));
  }

  @Test
  public void anotherTestEmpty() {
    Single.just(false)
        .filter(Boolean::booleanValue)
        .subscribe(empty -> logger.info("Null: {}", empty));
  }

  @Test
  public void testIsEmpty() {
    Maybe.fromCallable(() -> null)
        .isEmpty()
        .subscribe(empty -> logger.info("Null: {}", empty));
  }

  @Test
  public void testMitigateEmptyButGetKaboom() {
    Single.just(false)
        .filter(Boolean::booleanValue)
        .flatMapSingle(aBoolean -> {
          logger.info("Is this run? {}", aBoolean);
          return Single.just(aBoolean);
        })
        .subscribe(aBoolean -> logger.info("How about this? {}", aBoolean));
  }

  @Test
  public void testExplainMeLikeImFive() {
    Maybe.empty()
        .flatMapSingle(o -> {
          logger.info("Is this run? {}", o);
          return Single.just(o);
        })
        .subscribe(o -> logger.info("How about this? {}", o));
  }

  @Test
  public void testExplainMeLikeImFour() {
    Maybe.empty()
        .toSingle()
        .subscribe(o -> logger.info("How about this? {}", o));
  }

  @Test
  public void testCallMommy() {
    Maybe.empty()
        .toSingle()
        .subscribe(o -> logger.info("How about this? {}", o));
  }

  @Test
  public void willFlatMapActivateKaboom() {
    Maybe.empty()
        .flatMapSingle(o -> {
          logger.info("Is this run? {}", o);
          return Single.just(o);
        })
        .subscribe(o -> logger.info("How about this? {}", o));
  }

  @Test
  public void justIgnore() {
    Maybe.empty()
        .ignoreElement()
        .andThen(Observable.just("Oh Hai"))
        .subscribe(o -> logger.info("How about this? {}", o));
  }

  @Test
  public void alsoIgnored() {
    Maybe.empty()
        .toObservable()
        .subscribe(o -> logger.info("How about this? {}", o));
  }

  @Test
  public void alsoAlsoIgnored() {
    Maybe.empty()
        .flatMapObservable(o -> {
          logger.info("Is this run? {}", o);
          return Observable.never();
        })
        .subscribe(o -> logger.info("How about this? {}", o));
  }

  @Test
  public void singleToMaybe() {
    Single.just("333")
        .map(s -> null)
        .toMaybe()
        .subscribe(o -> logger.info("How about this? {}", o));
  }
}
