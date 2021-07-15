package com.github.nenomm.marbles;

import io.reactivex.Maybe;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CachingAttempt1 {

  private static final Logger logger = LoggerFactory.getLogger(CachingAttempt1.class);

  @Test
  public void cachingWithConcat() {
    Maybe<String> cached = Maybe.empty();
    Maybe<String> forReal = Maybe.just("VALUE");
    //Maybe<String> forReal = Maybe.empty();

    Maybe
        .concat(cached, forReal)
        .singleElement()
        .doOnComplete(() -> logger.info("And Beren put forth his left hand, slowly opening its fingers; but it was empty."))
        .doOnSuccess(s -> logger.info("Value is {}",s))
        .subscribe();
  }
}