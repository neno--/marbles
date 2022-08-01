package com.github.nenomm.marbles;

import io.reactivex.Observable;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObservableWithUglyDelay {

  private static final Logger logger = LoggerFactory.getLogger(ObservableWithUglyDelay.class);
  private static Random random = new Random();

  @Test
  public void uglyDelay() throws InterruptedException {
    Observable.just("A", "B", "C", "D", "E")
        .doOnNext(string -> {
          final int sleep = getSecs();
          logger.info("1. sleep for {}: {}", sleep, string);
          Thread.sleep(sleep);
        })
        .map(string -> {
          logger.info("Doing 1. work for {}", string);
          return string;
        })
        .doOnNext(string -> {
          final int sleep = getSecs();
          logger.info("2. sleep for {}: {}", sleep, string);
          Thread.sleep(sleep);
        })
        .map(string -> {
          logger.info("Doing 2. work for {}", string);
          return string;
        })
        .doOnNext(string -> {
          final int sleep = getSecs();
          logger.info("3. sleep for {}: {}", sleep, string);
          Thread.sleep(sleep);
        })
        .map(string -> {
          logger.info("Doing 3. work for {}", string);
          return string;
        })
        .subscribe();

    Thread.sleep(30000);
  }

  private static int getSecs() {
    return random.ints(1000, 5000)
        .findFirst()
        .getAsInt();
  }

}
