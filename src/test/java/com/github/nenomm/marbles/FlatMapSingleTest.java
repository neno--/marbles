package com.github.nenomm.marbles;

import io.reactivex.Observable;
import io.reactivex.ObservableOperator;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlatMapSingleTest {

  private static final Logger logger = LoggerFactory.getLogger(FlatMapSingleTest.class);

  @Test
  public void fromLetterToSingle() throws InterruptedException {
    Observable
        .just("A", "B", "C")
        .repeat()
        .take(5)
        .doOnComplete(() -> logger.info("It is done"))
        .flatMapSingle(letter -> {
          logger.info("Doing flatmap stuff");
          return Single.just(new ArrayList<String>(3));
        })
        .subscribe(s -> {
          logger.info("Subscribed.");
        });
  }
}
