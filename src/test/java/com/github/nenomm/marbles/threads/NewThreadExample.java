package com.github.nenomm.marbles.threads;

import io.reactivex.Observable;
import io.reactivex.ObservableOperator;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewThreadExample {

  private static final Logger logger = LoggerFactory.getLogger(NewThreadExample.class);

  @Test
  public void newThreadTest() throws InterruptedException {
    Observable
        .just(1, 2, 3, 4, 5, 6, 7, 8, 9)
        .doOnNext(integer -> logger.info("About to consume {}", integer))
        .flatMap(integer -> Observable.just(integer).observeOn(Schedulers.newThread()))
        .subscribe(integer -> logger.info("Consuming {}", integer));

    Thread.sleep(10000);
  }

  // and this is how you parallelize things!
  @Test
  public void anotherThreadTest() throws InterruptedException {
    Observable
        .just(1, 2, 3, 4, 5, 6, 7, 8, 9)
        .doOnNext(integer -> logger.info("About to consume {}", integer))
        .flatMap(integer ->
            Observable.just(integer)
                .lift(delay())
                .subscribeOn(Schedulers.newThread()))
        .subscribe(integer -> logger.info("Consuming {}", integer));

    Thread.sleep(15000);
  }

  private ObservableOperator<Integer, Integer> delay() {
    return new ObservableOperator<Integer, Integer>() {
      @NonNull
      @Override
      public Observer<? super Integer> apply(@NonNull Observer<? super Integer> observer) throws Exception {
        return new Observer<Integer>() {
          @Override
          public void onSubscribe(@NonNull Disposable d) {
            observer.onSubscribe(d);
          }

          @Override
          public void onNext(@NonNull Integer o) {
            logger.info("Delaying {}", o);
            try {
              Thread.sleep(1000);
              observer.onNext(o);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }

          @Override
          public void onError(@NonNull Throwable e) {
            observer.onError(e);
          }

          @Override
          public void onComplete() {
            observer.onComplete();
          }
        };
      }
    };
  }
}
