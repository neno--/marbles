package com.github.nenomm.marbles;

import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import java.util.function.Supplier;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IgnoreElementIntro {

  private static final Logger logger = LoggerFactory.getLogger(IgnoreElementIntro.class);

  Supplier<CompletableObserver> gimme = () -> {
    return new CompletableObserver() {

      @Override
      public void onSubscribe(Disposable d) {
        logger.info("Subscribed...");
      }

      @Override
      public void onComplete() {
        logger.info("completed");
      }

      @Override
      public void onError(Throwable e) {
        logger.error("KaBoom", e);
      }
    };
  };


  @Test
  public void testIgnore() {
    Single<String> mySingle = Single.fromCallable(() -> {
      return "not interested";
    });

    mySingle.ignoreElement().subscribe(gimme.get());
  }

  @Test
  public void testKaBoom() {
    Single<String> mySingle = Single.fromCallable(() -> {
      return null;
    });

    mySingle.ignoreElement().subscribe(gimme.get());
  }

  // ignore element for the whole range

}
