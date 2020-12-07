package com.github.nenomm.marbles;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleIntro {

  private static final Logger logger = LoggerFactory.getLogger(SingleIntro.class);

  @Test
  public void justCreateSingle() {
    Single<String> single = Single.create(emitter -> {
      logger.info("Emitting value...");
      emitter.onSuccess("singleValue");
    });

    single.subscribe(new SingleObserver<String>() {
      @Override
      public void onSubscribe(Disposable d) {
        logger.info("Subscribed...");
      }

      @Override
      public void onSuccess(String s) {
        logger.info("Success {}", s);
      }

      @Override
      public void onError(Throwable e) {
        logger.error("KaBomm", e);
      }
    });

    logger.info("Done!");
  }

  @Test
  public void scheduledTest() throws InterruptedException {
    Single<String> single = Single.create(emitter -> {
      logger.info("Waiting in emitter...");
      Thread.sleep(5000);
      emitter.onSuccess("singleValue");
    });

    // why is this needed
    single.observeOn(Schedulers.io());

    single.subscribe(new SingleObserver<String>() {
      @Override
      public void onSubscribe(Disposable d) {
        logger.info("Subscribed...");
      }

      @Override
      public void onSuccess(String s) {
        logger.info("Success {}", s);
      }

      @Override
      public void onError(Throwable e) {
        logger.error("KaBomm", e);
      }
    });

    logger.info("Done!");
  }

  @Test
  public void testNever() {
    Single<String> never = Single.never();

    logger.info("Created never!");

    never.subscribe(new SingleObserver<String>() {
      @Override
      public void onSubscribe(Disposable d) {
        logger.info("Subscribed...");
      }

      @Override
      public void onSuccess(String s) {
        logger.info("Success {}", s);
      }

      @Override
      public void onError(Throwable e) {
        logger.error("KaBomm", e);
      }
    });

    logger.info("Done!");
  }
}
