package com.github.nenomm.reactor;

import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class SideeffectInSubscription {

  private static final Logger logger = LoggerFactory.getLogger(SideeffectInSubscription.class);

  @Test
  void asyncSideEffect() throws InterruptedException {

    final CountDownLatch latch = new CountDownLatch(2);

    Mono.just(42)
        .doOnNext(integer -> logger.info("It is: {}", integer))
        .doOnNext(integer -> Mono.fromRunnable(() -> {
              try {
                Thread.sleep(5000);
                logger.info("Finished with sleeping");
                latch.countDown();
              } catch (InterruptedException e) {
                throw new RuntimeException(e);
              }
            }).subscribeOn(Schedulers.boundedElastic())
            .subscribe())
        .subscribe(integer -> {
          logger.info("Came here");
          latch.countDown();
        });

    logger.info("Again in main");
    latch.await();
    logger.info("---END---");
  }
}
