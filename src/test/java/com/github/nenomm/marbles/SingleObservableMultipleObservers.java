package com.github.nenomm.marbles;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleObservableMultipleObservers {

  private static final Logger logger = LoggerFactory.getLogger(SingleObservableMultipleObservers.class);

  @Test
  public void justCreateObservable() {
    Observable<String> obs = Observable.<String>create(observableEmitter -> {
      logger.info("Creating a");
      observableEmitter.onNext("a");
      logger.info("Creating b");
      observableEmitter.onNext("b");
      logger.info("Creating c");
      observableEmitter.onNext("c");
      observableEmitter.onComplete();
    });

    logger.info("Calling first observable");

    obs.subscribe(new Observer<String>() {
      @Override
      public void onSubscribe(Disposable d) {
        logger.info("Subscribed 1");
      }

      @Override
      public void onNext(String s) {
        logger.info("ON NEXT 1: {}", s);
      }

      @Override
      public void onError(Throwable e) {
        logger.error("ERROR 1: ", e);
      }

      @Override
      public void onComplete() {
        logger.info("completed 1");
      }
    });

    obs.subscribe(new Observer<String>() {
      @Override
      public void onSubscribe(Disposable d) {
        logger.info("Subscribed 2");
      }

      @Override
      public void onNext(String s) {
        logger.info("ON NEXT 2: {}", s);
      }

      @Override
      public void onError(Throwable e) {
        logger.error("ERROR 2: ", e);
      }

      @Override
      public void onComplete() {
        logger.info("completed 2");
      }
    });
  }
}
