package com.github.nenomm.marbles;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelayedObservableMultipleObservers {

  private static final Logger logger = LoggerFactory.getLogger(DelayedObservableMultipleObservers.class);

  @Test
  public void multipleObserversDifferentThreads() {
    Observable<String> source = Observable.<String>create(observableEmitter -> {
      logger.info("Creating a");
      observableEmitter.onNext("a");
      logger.info("Creating b");
      observableEmitter.onNext("b");
      logger.info("Creating c");
      observableEmitter.onNext("c");
      observableEmitter.onComplete();
    });

    logger.info("Starting up...");
    Observer<String> first = getObserver("first");
    Observer<String> second = getObserver("second");

    Thread firstThread = new Thread(() -> {
      logger.info("In thread");
      delay(1000);
      logger.info("Executing");
      source.subscribe(first);
    });

    Thread secondThread = new Thread(() -> {
      logger.info("In thread");
      delay(5000);
      logger.info("Executing");
      source.subscribe(second);
    });

    firstThread.start();
    secondThread.start();

    try {
      firstThread.join();
      secondThread.join();
    } catch (InterruptedException e) {
      logger.error("Interrupted!", e);
    }
  }

  private static void delay(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private static Observer<String> getObserver(String observerId) {
    return new Observer<String>() {
      @Override
      public void onSubscribe(Disposable d) {
        logger.info("Subscribed {}", observerId);
      }

      @Override
      public void onNext(String s) {
        logger.info("Next is: {} for {}", s, observerId);
      }

      @Override
      public void onError(Throwable e) {
        logger.info("KaBoom {}", observerId, e);
      }

      @Override
      public void onComplete() {
        logger.info("Completed for {}.", observerId);
      }
    };
  }
}