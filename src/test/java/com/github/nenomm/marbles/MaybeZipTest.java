package com.github.nenomm.marbles;

import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaybeZipTest {

  private static final Logger logger = LoggerFactory.getLogger(ObservableIntro.class);

  @Test
  public void testZip() {
    Maybe.zip(words(), numbers(), (s, integer) -> {
      return s + " " + integer;
    })
        .subscribe(new MaybeObserver<String>() {
          @Override
          public void onSubscribe(@NonNull Disposable d) {
            logger.info("Subscribed");
          }

          @Override
          public void onSuccess(@NonNull String s) {
            logger.info("Next: {}", s);
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
    Maybe.zip(Maybe.just("ONE"), numbers(), (s, integer) -> {
      return s + " " + integer;
    })
        .subscribe(new MaybeObserver<String>() {
          @Override
          public void onSubscribe(@NonNull Disposable d) {
            logger.info("Subscribed");
          }

          @Override
          public void onSuccess(@NonNull String s) {
            logger.info("Next: {}", s);
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
  public void testEmpty() {
    Maybe.zip(Maybe.empty(), numbers(), (s, integer) -> {
      return s + " " + integer;
    })
        .subscribe(new MaybeObserver<String>() {
          @Override
          public void onSubscribe(@NonNull Disposable d) {
            logger.info("Subscribed");
          }

          @Override
          public void onSuccess(@NonNull String s) {
            logger.info("Next: {}", s);
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

  Maybe<String> words() {
    return Maybe.create(emitter -> {
      new Thread(() -> {
        sleep(4000);
        emitter.onSuccess("A");
      }).start();
    });
  }

  Maybe<Integer> numbers() {
    return Maybe.create(emitter -> {
      new Thread(() -> {
        sleep(1500);
        emitter.onSuccess(1);
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
