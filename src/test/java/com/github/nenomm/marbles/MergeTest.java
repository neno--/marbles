package com.github.nenomm.marbles;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MergeTest {

  private static final Logger logger = LoggerFactory.getLogger(ObservableIntro.class);

  @Test
  public void testMerge() {
    Observable.merge(words(), numbers())
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
        sleep(500);
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
