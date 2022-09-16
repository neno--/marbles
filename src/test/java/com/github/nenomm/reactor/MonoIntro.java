package com.github.nenomm.reactor;

import java.util.Objects;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class MonoIntro {

  private static final Logger logger = LoggerFactory.getLogger(MonoIntro.class);

  Supplier<Subscriber<String>> gimme = () -> {
    return new Subscriber<String>() {
      private boolean empty = true;

      @Override
      public void onSubscribe(Subscription subscription) {
        subscription.request(Long.MAX_VALUE);
        logger.info("OBSERVER: Subscribed...");
      }

      @Override
      public void onNext(String s) {
        empty = false;
        logger.info("OBSERVER: Success {}", s);
      }

      @Override
      public void onError(Throwable e) {
        logger.error("OBSERVER: KaBoom", e);
      }

      @Override
      public void onComplete() {
        if (empty) {
          logger.info("OBSERVER: EMPTY!");
        } else {
          logger.info("OBSERVER: End of stream");
        }
      }
    };
  };

  @Test
  public void justCreateMono() {
    Mono<String> mono = Mono.create(emitter -> emitter.success("word"));

    mono.subscribe(gimme.get());

    logger.info("Done!");
  }

  @Test
  public void scheduledTest() throws InterruptedException {
    final Scheduler scheduler = Schedulers.newParallel("parallel-scheduler", 4);

    Mono<String> mono = Mono.create(emitter -> {
      logger.info("Waiting in emitter...");
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        emitter.error(e);
      }
      logger.info("Sending to emitter...");
      emitter.success("singleValue");
    });

    mono
        //.publishOn(scheduler)
        .subscribeOn(scheduler)
        .subscribe(gimme.get());

    logger.info("I am here.");

    Thread.sleep(3000);
    logger.info("Done!");
  }

  @Test
  public void testSingle() {
    Mono<String> mono = Mono.just("A");
    logger.info("Created Single!");
    mono.subscribe(gimme.get());
    logger.info("Done!");
  }

  @Test
  public void testNever() {
    Mono<String> mono = Mono.never();
    logger.info("Created never!");
    mono.subscribe(gimme.get());
    logger.info("Done!");
  }

  @Test
  public void testEmpty() {
    Mono<String> mono = Mono.empty();
    logger.info("Created empty!");
    mono.subscribe(gimme.get());
    logger.info("Done!");
  }

  @Test
  public void testNull() {
    Mono<String> mono = Mono.justOrEmpty(null);
    logger.info("Created null!");
    mono.subscribe(gimme.get());
    logger.info("Done!");
  }

  @Test
  public void testKaBoom() {
    Mono<String> mono = Mono.fromCallable(() -> {
      throw new RuntimeException("KaBoom!");
    });

    mono.subscribe(gimme.get());
  }

  // no kaboom, this actually works in reactor, as opposed to rxjava
  @Test
  public void testKaBoomAnother() {
    Mono<String> mono = Mono.fromCallable(() -> {
      return null;
    });

    mono.subscribe(gimme.get());
  }

  @Test
  public void monoMapBlocking() {
    Mono.just(1)
        .map(i -> {
          logger.info("in map");
          sleep(2000);
          return i * 2;
        })
        .map(Objects::toString)
        .subscribe(gimme.get());
    logger.info("I am here");
    sleep(5000);
    logger.info("Done");
  }

  @Test
  public void monoMap() {
    Mono.just(1)
        .flatMap(i -> {
          logger.info("in flatMap");
          sleep(2000);
          return Mono.just(i * 2);
        })
        .map(Objects::toString)
        .subscribe(gimme.get());
    logger.info("I am here");
    sleep(5000);
    logger.info("Done");
  }

  @Test
  public void notReallyAsyncOnlyLazyCall() {
    Mono.defer(() -> Mono.just(new Supplier<String>() {
          @Override
          public String get() {
            logger.info("going to sleep...");
            sleep(2000);
            logger.info("wakey wakey");
            return "YAAAAWN";
          }
        }.get()))
        .doOnNext(s -> logger.info("onNext"))
        .subscribe(gimme.get());

    logger.info("I am here");
    sleep(5000);
    logger.info("done");
  }

  @Test
  public void anotherNotReallyAsyncOnlyLazyCall() {
    Mono.fromCallable(() -> {
          logger.info("going to sleep...");
          sleep(2000);
          logger.info("wakey wakey");
          return "YAAAAWN";
        })
        .doOnNext(s -> logger.info("onNext"))
        .subscribe(gimme.get());

    logger.info("I am here");
    sleep(5000);
    logger.info("done");
  }

  @Test
  public void zipIt() {
    Mono.zip(Mono.just(1), Mono.just(2), (i, j) -> i + j)
        .map(Objects::toString)
        .subscribe(gimme.get());
  }

  @Test
  public void zipKaBoom() {
    Mono.zip(Mono.just(1), Mono.empty(), (i, j) -> i + (Integer) j)
        .map(Objects::toString)
        .subscribe(gimme.get());
  }

  private static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
