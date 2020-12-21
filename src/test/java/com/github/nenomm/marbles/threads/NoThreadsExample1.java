package com.github.nenomm.marbles.threads;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoThreadsExample1 {

  private static final Logger logger = LoggerFactory.getLogger(NoThreadsExample1.class);

  @Test
  public void noThreadsSequential() {
    Observable
        .create(emitter -> {
          int i = 0;
          while (!emitter.isDisposed()) {
            logger.info("Generating {}", i);
            emitter.onNext(i++);
          }
        })
        .take(10)
        .subscribe(o -> logger.info("There it is {}", o));
  }


  /*
      More than 10 numbers generated
   */
  @Test
  public void extraTreadFirstCase() {
    Observable
        .create(emitter -> {
          int i = 0;
          while (!emitter.isDisposed()) {
            logger.info("Generating {}", i);
            emitter.onNext(i++);
          }
        })
        .observeOn(Schedulers.computation())
        .take(10)
        .subscribe(o -> logger.info("There it is {}", o));
  }

  /*
      Exactly 10 numbers generated
   */
  @Test
  public void extraTreadSecondCase() {
    Observable
        .create(emitter -> {
          int i = 0;
          while (!emitter.isDisposed()) {
            logger.info("Generating {}", i);
            emitter.onNext(i++);
          }
        })
        .take(10)
        .observeOn(Schedulers.computation())
        .subscribe(o -> logger.info("There it is {}", o));
  }
}
