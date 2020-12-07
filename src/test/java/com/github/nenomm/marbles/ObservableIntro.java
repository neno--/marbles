package com.github.nenomm.marbles;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObservableIntro {

  private static final Logger logger = LoggerFactory.getLogger(ObservableIntro.class);

  @Test
  public void justCreateObservable() {
    Observable<String> first = Observable.just("Hello");

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
    second.subscribe(new Observer<String>() {
      @Override
      public void onSubscribe(Disposable d) {
        logger.info("Subscribed");
      }

      @Override
      public void onNext(String s) {
        logger.info("ON NEXT: {}", s);
      }

      @Override
      public void onError(Throwable e) {
        logger.error("ERROR: ", e);
      }

      @Override
      public void onComplete() {
        logger.info("completed");
      }
    });
  }

  @Test
  public void delayedExecution() throws InterruptedException {
    MutableObject<Thread> thread = new MutableObject<Thread>();

    Observable<String> lazy = Observable.<String>create(emitter -> {
      logger.info("[IN CREATE] oh, hai");
      Thread t = new Thread(() -> {
        try {
          Thread.sleep(1000);
          logger.info("[IN CREATE] a");
          emitter.onNext("a");
          Thread.sleep(1000);
          logger.info("[IN CREATE] b");
          emitter.onNext("b");
          Thread.sleep(1000);
          logger.info("[IN CREATE] c");
          emitter.onNext("c");
        } catch (InterruptedException e) {
          logger.info("KaBoom", e);
        }
      });
      thread.setValue(t);
      t.start();
      logger.info("[IN CREATE] CREATE FINISHED");
    });

    logger.info("Gonna subscribe...");
    Thread.sleep(5000);

    // execute the observable
    lazy.subscribe(new Observer<String>() {
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

    thread.getValue().join();
    logger.info("LAST LINE...");
  }
}
