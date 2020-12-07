package com.github.nenomm.marbles;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipTest {

  private static final Logger logger = LoggerFactory.getLogger(ObservableIntro.class);

  @Test
  public void testZip() {
    Observable.zip(words(), numbers(), (s, integer) -> {
      return s + " " + integer;
    })
        .subscribe(new Observer<Object>() {
          @Override
          public void onSubscribe(@NonNull Disposable d) {
            logger.info("Subscribed");
          }

          @Override
          public void onNext(@NonNull Object o) {
            logger.info("Next: {}", o);
          }

          @Override
          public void onError(@NonNull Throwable e) {
            logger.error("Error: {}", e);
          }

          @Override
          public void onComplete() {
            logger.info("Completed");
          }
        });

    sleep(5000);
    logger.info("DONE");
  }

  @Test
  public void testFastZip() {
    Observable.zip(Observable.just("ONE"), numbers(), (s, integer) -> {
      return s + " " + integer;
    })
        .subscribe(new Observer<Object>() {
          @Override
          public void onSubscribe(@NonNull Disposable d) {
            logger.info("Subscribed");
          }

          @Override
          public void onNext(@NonNull Object o) {
            logger.info("Next: {}", o);
          }

          @Override
          public void onError(@NonNull Throwable e) {
            logger.error("Error: {}", e);
          }

          @Override
          public void onComplete() {
            logger.info("Completed");
          }
        });

    sleep(5000);
    logger.info("DONE");
  }

  @Test
  public void testSingleZip() {
    Single.zip(Single.<String>create(emitter -> emitter.onSuccess("DONE")), Single.never(), (s, integer) -> {
      return s + " " + integer;
    })
        .subscribe(new SingleObserver<String>() {
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

    sleep(5000);
    logger.info("DONE");
  }

  Observable<String> words() {
    return Observable.create(emitter -> {
      new Thread(() -> {
        sleep(1000);
        emitter.onNext("A");
        sleep(1000);
        emitter.onNext("B");
        sleep(1000);
        emitter.onComplete();
      }).start();
    });
  }

  Observable<Integer> numbers() {
    return Observable.create(emitter -> {
      new Thread(() -> {
        sleep(1500);
        emitter.onNext(1);
        sleep(500);
        emitter.onNext(2);
        sleep(500);
        emitter.onNext(3);
        sleep(500);
        emitter.onComplete();
      }).start();
    });
  }

  private static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      logger.error("Wakeup!", e);
    }
  }
}
