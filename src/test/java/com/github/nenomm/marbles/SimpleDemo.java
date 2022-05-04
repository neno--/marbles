package com.github.nenomm.marbles;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleDemo {

  private static final Logger logger = LoggerFactory.getLogger(SimpleDemo.class);

  @Test
  public void test() throws InterruptedException {

    final Completable task2 = Completable.fromRunnable(() -> {
          logger.info("Here I am!");
        })
        .subscribeOn(Schedulers.computation());

    final Completable task1 = Completable.fromRunnable(() -> {
          try {
            Thread.sleep(5000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          logger.info("Me too!");
        })
        .subscribeOn(Schedulers.computation());

    //task1.subscribe();
    //task2.subscribe();

    task1.andThen(task2).subscribe();


    Thread.sleep(6000);

    Maybe.just(1)
        .map(nesto -> {
          int i = Maybe.just(33).blockingGet();
          return 5;
        });


  }
  @Test
  public void test1() throws InterruptedException {

    Single.just(1)
            .map(i -> {
              Thread.sleep(10000);
              return 5*i;
            })
        .subscribeOn(Schedulers.computation())
                .subscribe();

    Thread.sleep(15000);
  }
}