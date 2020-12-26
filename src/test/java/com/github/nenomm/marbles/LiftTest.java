package com.github.nenomm.marbles;

import io.reactivex.Observable;
import io.reactivex.ObservableOperator;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// implementing totally new operator out of thin air
// lift allows transforming subscribers
public class LiftTest {

  private static final Logger logger = LoggerFactory.getLogger(LiftTest.class);

  @Test
  public void fromLetter() throws InterruptedException {
    Observable
        .just("A", "B", "C")
        .repeat()
        .take(20)
        //.observeOn(Schedulers.newThread())
        .lift(everyThird())
        //.observeOn(Schedulers.computation()) ???
        .doOnNext(character -> logger.info("{}", character.toString()))
        .subscribe();

    Thread.sleep(5);
  }

  ObservableOperator<Character, String> everyThird() {
    return new ObservableOperator<Character, String>() {

      @NonNull
      @Override
      public Observer<? super String> apply(@NonNull Observer<? super Character> observer) throws Exception {
        return new Observer<String>() {
          private int counter = 0;
          private boolean subscribed = false;

          @Override
          public void onSubscribe(@NonNull Disposable d) {
            logger.info("ON SUBSCRIBE");
            observer.onSubscribe(d);
            subscribed = true;
          }

          @Override
          public void onNext(@NonNull String s) {
            if (subscribed) {
              logger.info("ON NEXT");
              if (++counter % 3 == 0) {
                //observer.onNext(s);
                observer.onNext(s.charAt(0));
                counter = 0;
              }
            }
          }

          @Override
          public void onError(@NonNull Throwable e) {
            if (subscribed) {
              logger.info("ON ERROR");
              observer.onError(e);
            }
          }

          @Override
          public void onComplete() {
            if (subscribed) {
              logger.info("ON COMPLETE");
              observer.onComplete();
            }
          }
        };
      }
    };
  }
}
