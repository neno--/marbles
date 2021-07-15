package com.github.nenomm.marbles;

import io.reactivex.Observable;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleOperator {

  private static final Logger logger = LoggerFactory.getLogger(SingleOperator.class);

  @Test
  public void justCreateSingle() {
    Observable
        .range(1, 1)
        .singleOrError()
        .subscribe(integer -> logger.info("Value {}", integer));
  }

  @Test
  public void justCreateDefaultSingle() {
    Observable
        .range(1, 0)
        .single(99)
        .subscribe(integer -> logger.info("Value {}", integer));
  }
}
