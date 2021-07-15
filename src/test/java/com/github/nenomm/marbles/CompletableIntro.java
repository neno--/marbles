package com.github.nenomm.marbles;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.disposables.Disposable;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompletableIntro {

  private static final Logger logger = LoggerFactory.getLogger(CompletableIntro.class);

  @Test
  public void justCreateSCompletable() {
    Completable completable = Completable.create(new CompletableOnSubscribe() {
      @Override
      public void subscribe(CompletableEmitter emitter) throws Exception {
        emitter.onComplete();
      }
    });

    completable.subscribe(new CompletableObserver() {
      @Override
      public void onSubscribe(Disposable d) {

      }

      @Override
      public void onComplete() {

      }

      @Override
      public void onError(Throwable e) {

      }
    });

    logger.info("Done!");
  }
}
