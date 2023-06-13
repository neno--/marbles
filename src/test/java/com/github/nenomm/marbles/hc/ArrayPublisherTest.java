package com.github.nenomm.marbles.hc;

import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

public class ArrayPublisherTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(ArrayPublisherTest.class);

  @Test
  public void justConsumeAndLog() throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1);
    final ArrayPublisher<Integer> arrayPublisher = new ArrayPublisher<Integer>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9});

    Flux.from(arrayPublisher)
        .doOnNext(integer -> LOGGER.info("Received {}", integer))
        .doOnNext(integer -> {

          if (integer == 4) {
            LOGGER.info("Cancelling subscription");

          }
        })
        .doAfterTerminate(() -> {
          LOGGER.info("Decrementing latch");
          latch.countDown();
        })
        .subscribe(integer -> LOGGER.info("Processing {}", integer));

    LOGGER.info("Waiting for termination of the flux");
    latch.await();
    LOGGER.info("Done");
  }

  @Test
  public void testRecursion() throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1);
    final ArrayPublisher<Integer> arrayPublisher = new ArrayPublisher<Integer>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9});

    arrayPublisher
        .subscribe(new Subscriber<Integer>() {
          Subscription subscription;

          @Override
          public void onSubscribe(Subscription subscription) {
            LOGGER.info("On subscribe");
            this.subscription = subscription;
            subscription.request(2);
          }

          @Override
          public void onNext(Integer integer) {
            LOGGER.info("On next");
            this.subscription.request(2);
          }

          @Override
          public void onError(Throwable throwable) {

          }

          @Override
          public void onComplete() {
            LOGGER.info("On complete");
            latch.countDown();
          }
        });

    LOGGER.info("Waiting for termination of the flux");
    latch.await();
    LOGGER.info("Done");
  }

  @Test
  public void cancelSubscriptionAfter7() throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1);
    final ArrayPublisher<Integer> arrayPublisher = new ArrayPublisher<Integer>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9});

    arrayPublisher
        .subscribe(new Subscriber<Integer>() {
          int counter;
          Subscription subscription;

          @Override
          public void onSubscribe(Subscription subscription) {
            LOGGER.info("On subscribe");
            this.subscription = subscription;
            subscription.request(1);
          }

          @Override
          public void onNext(Integer integer) {
            counter++;
            if (counter == 7) {
              LOGGER.info("Cancelling subscription");
              this.subscription.cancel();
              return;
            }
            LOGGER.info("On next");
            this.subscription.request(2);
          }

          @Override
          public void onError(Throwable throwable) {

          }

          @Override
          public void onComplete() {
            LOGGER.info("On complete");
            latch.countDown();
          }
        });

    LOGGER.info("Waiting for termination of the flux");
    latch.await();
    LOGGER.info("Done");
  }

}
