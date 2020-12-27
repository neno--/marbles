package com.github.nenomm.marbles;

import io.reactivex.Observable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Timed {

  private static final Logger logger = LoggerFactory.getLogger(Timed.class);

  @Test
  public void withTimer() throws InterruptedException {
    Observable ob = Observable.timer(1, TimeUnit.SECONDS);

    ob.subscribe(aLong -> logger.info("There: {}", aLong));
    ob.subscribe(aLong -> logger.info("There: {}", aLong));

    Thread.sleep(10000);
  }

  @Test
  public void fromInterval() throws InterruptedException {
    Observable
        .interval(50, TimeUnit.MILLISECONDS)
        .take(10000, TimeUnit.MILLISECONDS)
        .doOnNext(aLong -> logger.info("Generated another one: {}", aLong))
        .map(aLong -> aLong % 5)
        .doOnNext(aLong -> logger.info("Mapped another one: {}", aLong))
        .buffer(3000, TimeUnit.MILLISECONDS)
        .flatMap(longs -> Observable.fromIterable(longs.stream().filter(distinctByKey(Function.identity())).collect(Collectors.toList())))
        .subscribe(aLong -> logger.info("End result: {}", aLong));

    Thread.sleep(15000);
  }

  // https://stackoverflow.com/questions/23699371/java-8-distinct-by-property
  public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(keyExtractor.apply(t));
  }

  @Test
  public void fromIntervalPerhapsAnotherTake() throws InterruptedException {
    Observable
        .interval(50, TimeUnit.MILLISECONDS)
        .take(10000, TimeUnit.MILLISECONDS)
        .doOnNext(aLong -> logger.info("Generated another one: {}", aLong))
        .map(aLong -> aLong % 5)
        .doOnNext(aLong -> logger.info("Mapped another one: {}", aLong))
        .buffer(3000, TimeUnit.MILLISECONDS)
        .flatMap(longs -> Observable
            .fromIterable(longs)
            .distinct(aLong -> aLong))
        .subscribe(aLong -> logger.info("End result: {}", aLong));

    Thread.sleep(15000);
  }
}
