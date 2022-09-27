package com.github.nenomm.reactor;


import static com.github.nenomm.marbles.SingleIntro.sleep;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class MonoToAsyncResult {

  private static final Logger logger = LoggerFactory.getLogger(MonoToAsyncResult.class);

  final Subscriber<String> munch = new Subscriber<String>() {
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

  static ConcurrentHashMap<Integer, CompletableFuture<String>> callbacks = new ConcurrentHashMap<>();

  Mono<String> registerAsyncCallback(int correlationId) {
    return Mono.fromFuture(() -> {
      final CompletableFuture<String> completableFuture = new CompletableFuture<>();
      callbacks.put(correlationId, completableFuture);
      return completableFuture;
    });
  }

  @Test
  public void registerAsyncCallback() {
    simulateEvents();
    Mono.just(42)
        .flatMap(this::registerAsyncCallback)
        .subscribe(munch);

    logger.info("I am here");
    sleep(5000);
    logger.info("done");
  }

  void simulateEvents() {
    Executors.newCachedThreadPool().submit(() -> {
      for (int i = 0; i < 100; i++) {
        sleep(40);
        final int found = i;
        Optional.ofNullable(callbacks.remove(i)).ifPresent(callbacks -> {
          callbacks.complete(String.format("I am done with: %s", found));
        });
      }
      callbacks.entrySet().stream()
          .forEach(entry -> entry.getValue().completeExceptionally(new RuntimeException(String.format("timed out for %s", entry.getKey()))));
      logger.info("Simulation ended.");
    });
  }
}
