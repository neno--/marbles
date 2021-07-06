package com.github.nenomm.marbles;

import static io.reactivex.Observable.timer;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import java.util.Random;
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
    Observable ob = timer(1, SECONDS);

    ob.subscribe(aLong -> logger.info("There: {}", aLong));
    ob.subscribe(aLong -> logger.info("There: {}", aLong));

    logger.info("Started");
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
  public void fromIntervalPerhapsAnotherTake() {
    final TestObserver<Long> observer = new TestObserver<>();

    Observable
        .interval(50, TimeUnit.MILLISECONDS)
        .take(10000, TimeUnit.MILLISECONDS)
        //.doOnNext(aLong -> logger.info("Generated another one: {}", aLong))
        .map(aLong -> aLong % 5)
        //.doOnNext(aLong -> logger.info("Mapped another one: {}", aLong))
        .buffer(20000, TimeUnit.MILLISECONDS)
        .flatMap(longs -> Observable
            .fromIterable(longs)
            .distinct(aLong -> aLong))
        //.subscribe(aLong -> logger.info("End result: {}", aLong));
        .subscribe(observer);

    observer.awaitTerminalEvent(15000, MILLISECONDS);

    observer.assertComplete();
    observer.assertNoErrors();
    observer.assertValueCount(5);
  }

  @Test
  public void delayTest() {
    final TestObserver<String> observer = new TestObserver<>();

    Observable
        .just("A", "B", "C")
        .delay(1, SECONDS)
        .doOnNext(logger::info)
        .subscribe(observer);

    observer.awaitTerminalEvent(5000, MILLISECONDS);

    observer.assertComplete();
    observer.assertNoErrors();
    observer.assertValueCount(3);
  }

  @Test
  public void anotherDelayTest() {
    final TestObserver<String> observer = new TestObserver<>();

    Observable
        .just("A", "B", "C")
        .delay(word -> timer(new Random().nextInt(3000), MILLISECONDS))
        .doOnNext(logger::info)
        .subscribe(observer);

    observer.awaitTerminalEvent(7000, MILLISECONDS);

    observer.assertComplete();
    observer.assertNoErrors();
    observer.assertValueCount(3);
  }

  @Test
  public void timerTest() {
    final TestObserver<Long> observer = new TestObserver<>();

    timer(1, SECONDS)
        .subscribe(aLong -> logger.info("{}", aLong));

    observer.awaitTerminalEvent(5000, MILLISECONDS);

    observer.assertComplete();
    observer.assertNoErrors();
    observer.assertValueCount(3);
  }
}
