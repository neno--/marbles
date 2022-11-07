package com.github.nenomm.reactor;


import static com.github.nenomm.marbles.SingleIntro.sleep;

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

  public static Supplier<Subscriber<String>> gimme = () -> {
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
        logger.info("OBSERVER: Success '{}'", s);
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
      sleep(1000);
      logger.info("Sending to emitter...");
      emitter.success("singleValue");
    });

    mono
        //.publishOn(scheduler)
        .subscribeOn(scheduler)
        .subscribe(gimme.get());

    logger.info("I am here.");

    sleep(3000);
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
  public void monoFlatMap() {
    Mono.just(1)
        .publishOn(Schedulers.parallel())
        .flatMap(i -> {
          logger.info("going to sleep...");
          sleep(2000);
          logger.info("wakey wakey");
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

  @Test
  public void onNext() {
    Mono.just(123)
        .doOnNext(integer -> logger.info("next"))
        .doOnTerminate(() -> logger.info("terminate"))
        .subscribe();
  }

  @Test
  public void onComplete() {
    Mono.empty()
        .doOnNext(integer -> logger.info("next"))
        .doOnSuccess(o -> logger.info("{}", o))
        .subscribe();
  }
}
