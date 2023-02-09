package com.github.nenomm.reactor;


import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class ChainingOperators {

  private static final Logger logger = LoggerFactory.getLogger(ChainingOperators.class);

  @Test
  public void filledMonoCanChain() {
    Mono.just("hello")
        .then(Mono.just(23))
        .subscribe(integer -> logger.info("Arrived {}", integer));
  }

  @Test
  public void emptyMonoCanChain() {
    Mono.empty()
        .then(Mono.just(23))
        .subscribe(integer -> logger.info("Arrived {}", integer));
  }

  @Test
  public void errorMonoCannotChain() {
    Mono.error(new RuntimeException("KaBoom"))
        .then(Mono.just(23))
        .subscribe(integer -> logger.info("Arrived {}", integer));
  }

}
