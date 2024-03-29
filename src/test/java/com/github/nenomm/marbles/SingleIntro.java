package com.github.nenomm.marbles;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.Objects;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleIntro {

  private static final Logger logger = LoggerFactory.getLogger(SingleIntro.class);

  Supplier<SingleObserver<String>> gimme = () -> {
    return new SingleObserver<String>() {
      @Override
      public void onSubscribe(Disposable d) {
        logger.info("OBSERVER: Subscribed...");
      }

      @Override
      public void onSuccess(String s) {
        logger.info("OBSERVER: Success {}", s);
      }

      @Override
      public void onError(Throwable e) {
        logger.error("OBSERVER: KaBoom", e);
      }
    };
  };

  @Test
  public void justCreateSingle() {
    Single<String> single = Single.create(emitter -> {
      logger.info("Emitting value...");
      emitter.onSuccess("singleValue");
    });

    single.subscribe(gimme.get());

    logger.info("Done!");
  }

  @Test
  public void scheduledTest() throws InterruptedException {
    Single<String> single = Single.create(emitter -> {
      logger.info("Waiting in emitter...");
      Thread.sleep(5000);
      emitter.onSuccess("singleValue");
    });

    // why is this needed
    single.observeOn(Schedulers.io());

    single.subscribe(gimme.get());

    logger.info("Done!");
  }

  @Test
  public void testNever() {
    Single<String> never = Single.never();

    logger.info("Created never!");

    never.subscribe(gimme.get());

    logger.info("Done!");
  }

  @Test
  public void testKaBoom() {
    Single<String> mySingle = Single.fromCallable(() -> {
      throw new RuntimeException("KaBoom!");
    });

    mySingle.subscribe(gimme.get());
  }

  @Test
  public void testKaBoomAnother() {
    Single<String> mySingle = Single.fromCallable(() -> {
      return null;
    });

    mySingle.subscribe(gimme.get());
  }

  @Test
  public void singleFlatMap() {
    Single.just(1)
        .observeOn(Schedulers.io())
        .flatMap(i -> {
          logger.info("going to sleep...");
          sleep(2000);
          logger.info("wakey wakey");
          return Single.just(i * 2);
        })
        .map(Objects::toString)
        //.subscribeOn(Schedulers.io())
        .subscribe(gimme.get());
    logger.info("I am here");
    sleep(5000);
    logger.info("Done");
  }

  public static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
