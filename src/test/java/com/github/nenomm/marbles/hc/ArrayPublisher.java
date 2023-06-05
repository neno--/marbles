package com.github.nenomm.marbles.hc;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArrayPublisher<T> implements Publisher<T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ArrayPublisher.class);
  private final T[] source;

  public ArrayPublisher(T[] source) {
    this.source = source;
  }

  @Override
  public void subscribe(Subscriber<? super T> subscriber) {
    subscriber.onSubscribe(new Subscription() {
      long requested = 0;
      long processed = 0;

      @Override
      public void request(long n) {
        if (requested > 0) {
          requested += n;
          LOGGER.info("It will be processed later :)");
          return;
        }
        requested += n;

        LOGGER.info("Just requested {} items, total {}", n, requested);

        while (requested > 0) {
          if (processed >= source.length) {
            LOGGER.info("I am done");
            subscriber.onComplete();
            LOGGER.info("You are done");
            return;
          }

          final T item = source[(int) processed];
          LOGGER.info("Publishing item {}: {}", processed + 1, item);
          requested--;
          processed++;
          subscriber.onNext(item);
        }
        LOGGER.info("Are we here sometimes?");
      }

      @Override
      public void cancel() {
        LOGGER.info("In cancel");
      }
    });

  }
}
