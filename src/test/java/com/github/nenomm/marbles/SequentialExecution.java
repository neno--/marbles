package com.github.nenomm.marbles;

import static io.reactivex.Observable.timer;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.internal.schedulers.ImmediateThinScheduler;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SequentialExecution {

  private static final Logger logger = LoggerFactory.getLogger(SequentialExecution.class);

  final Completable c = Single.just(42)
      .map(integer -> {
        logger.info("Working in map 1");
        Thread.sleep(1000);
        return integer;
      })
      .map(integer -> {
        logger.info("Working in map 2");
        Thread.sleep(1000);
        return integer;
      }).ignoreElement()
      .andThen(Completable.fromAction(() -> {
        logger.info("Working in something");
        Thread.sleep(1000);
      })).doOnComplete(() -> {
        logger.info("Working in complete");
        Thread.sleep(1000);
      })
      .doOnComplete(() -> {
        logger.info("All done");
      });

  @Test
  public void parallel() throws InterruptedException {
    logger.info("Hello from main!");

    c
        .subscribeOn(Schedulers.single())
        .subscribe();

    logger.info("Main ended");
  }

  @Test
  public void sequential() throws InterruptedException {
    logger.info("Hello from main!");

    c
        .subscribeOn(ImmediateThinScheduler.INSTANCE)
        .subscribe();

    logger.info("Main ended");
  }

  @Test
  public void sequential1() throws InterruptedException {
    logger.info("Hello from main!");
    CountDownLatch countDownLatch = new CountDownLatch(1);

    c
        .subscribeOn(Schedulers.computation())
        .subscribe(() -> countDownLatch.countDown());

    countDownLatch.await();
    logger.info("Main ended");
  }


}
