package com.github.nenomm.marbles;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnTerrorTest {

  private static final Logger logger = LoggerFactory.getLogger(OnTerrorTest.class);

  @Test
  public void justCreateSingle() {
    Observable
        .<String>fromCallable(() -> {
          throw new RuntimeException("Fire!!!");
        })
        .doOnError(throwable -> logger.error("Error happened: {}", throwable.getMessage()))
        .onErrorResumeNext((ObservableSource<String>) (observer -> observer.onNext("this is fine.")))
        .subscribe((String s) -> logger.info("Dog: {}", s));
  }
}
