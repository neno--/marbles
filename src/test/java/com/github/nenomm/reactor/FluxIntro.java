package com.github.nenomm.reactor;

import static com.github.nenomm.marbles.SingleIntro.sleep;

import io.reactivex.Observable;
import java.util.Objects;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class FluxIntro {

  private static final Logger logger = LoggerFactory.getLogger(FluxIntro.class);

  Supplier<Subscriber<String>> gimme = () -> {
    return new Subscriber<String>() {

      @Override
      public void onSubscribe(Subscription subscription) {
        subscription.request(Long.MAX_VALUE);
        logger.info("OBSERVER: Subscribed...");
      }

      @Override
      public void onNext(String s) {
        logger.info("OBSERVER: Success {}", s);
      }

      @Override
      public void onError(Throwable e) {
        logger.error("OBSERVER: KaBoom", e);
      }

      @Override
      public void onComplete() {
        logger.info("OBSERVER: Empty");
      }
    };
  };

  @Test
  public void justCreateFlux() {
    Flux<String> flux = Flux.just("A", "B", "C");

    flux.subscribe(gimme.get());

    logger.info("Done!");
  }

  @Test
  public void justCreateAnotherFlux() {
    Flux.just("red", "white", "blue")
        .map(String::toUpperCase)
        .subscribe(new Subscriber<String>() {

          @Override
          public void onSubscribe(Subscription s) {
            s.request(Long.MAX_VALUE);
          }
          @Override
          public void onNext(String t) {
            System.out.println(t);
          }
          @Override
          public void onError(Throwable t) {
          }
          @Override
          public void onComplete() {
          }

        });

    logger.info("Done!");
  }

  @Test
  public void observableFlatMap() {
    Flux.just(3,2,1)
        .flatMap(i -> {
          return Flux.just(i)
              .publishOn(Schedulers.parallel())
              .doOnNext(integer -> {
                logger.info("going to sleep...");
                sleep(i*1000);
                logger.info("wakey wakey");
              });
        })
        .map(Objects::toString)
        .subscribe(gimme.get());
    logger.info("I am here");
    sleep(10000);
    logger.info("Done");
  }
}
