package com.github.nenomm.marbles.zip;

import io.reactivex.Observable;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlowFastZipTest {

  private static final Logger logger = LoggerFactory.getLogger(SlowFastZipTest.class);

  @Test
  public void slowFastZipTest() {
    Observable slow = Observable
        .interval(100, TimeUnit.MILLISECONDS)
        .take(1000, TimeUnit.MILLISECONDS);

    Observable fast = Observable
        .interval(10, TimeUnit.MILLISECONDS)
        .take(1000, TimeUnit.MILLISECONDS)
        .map(aLong -> "STRING" + aLong);

    Observable.zip(slow, fast, (o, o1) -> o.toString() + o1.toString())
        .subscribe(o -> logger.info("Result: {}", o));

    sleep(5000);
    logger.info("DONE");
  }

  @Test
  public void combineLatestTest() {
    Observable slow = Observable
        .interval(100, TimeUnit.MILLISECONDS)
        .take(1000, TimeUnit.MILLISECONDS);

    Observable fast = Observable
        .interval(10, TimeUnit.MILLISECONDS)
        .take(1000, TimeUnit.MILLISECONDS)
        .map(aLong -> "STRING" + aLong);

    Observable.combineLatest(slow, fast, (o, o1) -> o.toString() + o1.toString())
        .subscribe(o -> logger.info("Result: {}", o));

    sleep(5000);
    logger.info("DONE");
  }


  private static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      logger.error("Wakeup!", e);
    }
  }
}
