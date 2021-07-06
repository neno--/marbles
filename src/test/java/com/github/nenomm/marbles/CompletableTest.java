package com.github.nenomm.marbles;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.CountDownLatch;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompletableTest {

  private static final Logger logger = LoggerFactory.getLogger(CompletableTest.class);

  @Test
  public void createCompletable() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);

    logger.info("Creating completable");

    Completable completable = Completable.fromRunnable(() -> {
      logger.info("Started");
      try {
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      if (true) {
        //throw new RuntimeException("Boom");
      }

      logger.info("Done");
    })
        .doOnComplete(() -> logger.info("ON COMPLETE"))
        .andThen((CompletableObserver observer) -> {
          logger.info("AND THEN");
          latch.countDown();
        })
        .subscribeOn(Schedulers.computation());

    logger.info("Starting completable");
    completable.subscribe();
    logger.info("End from main");
    latch.await();
  }
}
