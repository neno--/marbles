package com.github.nenomm.marbles.zip;

import com.github.nenomm.marbles.ObservableIntro;
import io.reactivex.MaybeObserver;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnotherZipTest {

  private static final Logger logger = LoggerFactory.getLogger(ObservableIntro.class);

  @Test
  public void testZipNoDelay() {
    Single.zip(Single.just("A"), Single.just(1), (a, b) -> a.toString() + b.toString())
        .subscribe(single());
  }

  @Test
  public void testZipMoreOperators() {
    Single.zip(Single.just("A"), Single.just(1).filter(i -> i < 10).toSingle(), (a, b) -> a.toString() + b.toString())
        .subscribe(single());
  }

  @Test
  public void testZipZeroDelay() {
    Single.zip(Single.just("A"), Single.just(1).delay(0, TimeUnit.MILLISECONDS), (a, b) -> a.toString() + b.toString())
        .subscribe(single());
  }

  private static Observer<Object> observer() {
    return new Observer<Object>() {
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
        logger.error("Error:", e);
      }

      @Override
      public void onComplete() {
        logger.info("Completed");
      }
    };
  }

  private static SingleObserver<Object> single() {
    return new SingleObserver<Object>() {
      @Override
      public void onSubscribe(@NonNull Disposable d) {
        logger.info("Subscribed");
      }

      @Override
      public void onSuccess(@NonNull Object o) {
        logger.info("Success: {}", o);
      }

      @Override
      public void onError(@NonNull Throwable e) {
        logger.error("Error:", e);
      }
    };
  }

  private static MaybeObserver<Object> maybe() {
    return new MaybeObserver<Object>() {
      @Override
      public void onSubscribe(@NonNull Disposable d) {

      }

      @Override
      public void onSuccess(@NonNull Object o) {

      }

      @Override
      public void onError(@NonNull Throwable e) {

      }

      @Override
      public void onComplete() {

      }
    };
  }
}
