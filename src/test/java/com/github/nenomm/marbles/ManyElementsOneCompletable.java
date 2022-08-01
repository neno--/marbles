package com.github.nenomm.marbles;

import io.reactivex.Completable;
import io.reactivex.Observable;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManyElementsOneCompletable {

  private static final Logger logger = LoggerFactory.getLogger(ManyElementsOneCompletable.class);

  @Test
  public void manyElementsOneCompletable() throws InterruptedException {
    Observable.just("A", "B", "C", "D", "E")
        .doOnNext(string -> logger.info("Working on {}", string))
        .ignoreElements()
        .andThen(Completable.fromRunnable(() -> logger.info("Oh hai!")))
        .doOnComplete(() -> logger.info("The End"))
        .subscribe();
  }
}
