package com.github.nenomm.reactor;


import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class CombiningMonos {

  private static final Logger logger = LoggerFactory.getLogger(CombiningMonos.class);

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
    Mono<String> mono1 = Mono.create(emitter -> emitter.success("word"));
    Mono<String> mono2 = Mono.just("hello");

    Mono.zip(mono1, Mono.empty())
        .map(tuple -> {
          logger.info("Heres a tuple: {}", tuple);
          return "hai";
        })
        .subscribe(gimme.get());

    logger.info("Done!");
  }
}
