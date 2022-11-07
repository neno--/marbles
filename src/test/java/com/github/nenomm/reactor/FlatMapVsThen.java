package com.github.nenomm.reactor;


import static com.github.nenomm.marbles.SingleIntro.sleep;

import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class FlatMapVsThen {

  private static final Logger logger = LoggerFactory.getLogger(FlatMapVsThen.class);
  final Scheduler scheduler = Schedulers.newParallel("parallel-scheduler", 4);

  @Test
  public void waitInFlatMap() throws InterruptedException {
    Mono<String> mono = Mono.create(emitter -> emitter.success("word"));
    final CountDownLatch latch = new CountDownLatch(1);

    mono
        .flatMap(ignored -> {
          logger.info("Something really slow is about to happen...");
          sleep(3000);
          logger.info("Slow finished!");
          return Mono.<String>create(emitter -> emitter.success("done"));
        })
        .subscribeOn(scheduler)
        .doAfterTerminate(latch::countDown)
        .subscribe(MonoIntro.gimme.get());

    latch.await();
    logger.info("Done!");
  }

  @Test
  public void waitInThen() throws InterruptedException {
    Mono<String> mono = Mono.create(emitter -> emitter.success("word"));
    final CountDownLatch latch = new CountDownLatch(1);

    mono
        .then(Mono.<String>create(emitter -> {
          logger.info("Something really slow is about to happen...");
          sleep(3000);
          logger.info("Slow finished!");
          emitter.success("done");
        }))
        .subscribeOn(scheduler)
        .doAfterTerminate(latch::countDown)
        .subscribe(MonoIntro.gimme.get());

    latch.await();
    logger.info("Done!");
  }
}
