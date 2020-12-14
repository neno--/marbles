package com.github.nenomm.marbles;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelayedObservable {

  private static final Logger logger = LoggerFactory.getLogger(DelayedObservable.class);

  @Test
  public void executeFromThread1() {
    Observable<String> second = Observable.<String>create(observableEmitter -> {
      logger.info("Creating a");
      observableEmitter.onNext("a");
      logger.info("Creating b");
      observableEmitter.onNext("b");
      logger.info("Creating c");
      observableEmitter.onNext("c");
      observableEmitter.onComplete();
    });

    logger.info("Calling second observable");

    second
        .doOnSubscribe(disposable -> logger.info("Subscribed"))
        .doOnNext(s -> logger.info("ON NEXT: {}", s))
        .doOnError(throwable -> logger.error("ERROR: ", throwable))
        .doOnComplete(() -> logger.info("completed"));

    Thread observerThread = new Thread(() -> {
      logger.info("In thread");
      delay(5000);
      logger.info("Executing");
      second.subscribe();
    });

    observerThread.start();

    try {
      observerThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void executeFromThread2() {
    Observable<String> second = Observable.<String>create(observableEmitter -> {
      logger.info("Creating a");
      observableEmitter.onNext("a");
      logger.info("Creating b");
      observableEmitter.onNext("b");
      logger.info("Creating c");
      observableEmitter.onNext("c");
      observableEmitter.onComplete();
    });

    logger.info("Calling second observable");

    Thread observerThread = new Thread(() -> {
      logger.info("In thread");
      delay(5000);
      logger.info("Executing");
      second.subscribe(new Observer<String>() {
        @Override
        public void onSubscribe(Disposable d) {
          logger.info("Subscribed");
        }

        @Override
        public void onNext(String s) {
          logger.info("Next is: {}", s);
        }

        @Override
        public void onError(Throwable e) {
          logger.info("KaBoom", e);
        }

        @Override
        public void onComplete() {
          logger.info("Completed.");
        }
      });
    });

    observerThread.start();

    try {
      observerThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static void delay(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}