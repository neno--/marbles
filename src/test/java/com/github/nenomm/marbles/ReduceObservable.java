package com.github.nenomm.marbles;

import io.reactivex.Observable;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReduceObservable {

  private static final Logger logger = LoggerFactory.getLogger(ReduceObservable.class);

  @Test
  public void justCreateObservablesForReduce() {
    Observable.just(0.1d, 0.3d, 0.5d, 0.9d)
        .reduce(0d, (acc, next) -> acc += next)
        .subscribe(sum -> logger.info("Sum is: {}", sum));
  }

  @Test
  public void justCreateObservablesForCollect() {
    Observable.just(0.1d, 0.3d, 0.5d, 0.9d)
        .collect(() -> 0d, (acc, next) -> acc += next)
        .subscribe(sum -> logger.info("Sum is: {}", sum));
    // this is wrong! Double is a value type. Collect requires a stateful accumulator.
  }
}
