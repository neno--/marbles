package com.github.nenomm.marbles;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.apache.commons.lang3.mutable.MutableObject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObservableIntro {

  private static final Logger logger = LoggerFactory.getLogger(ObservableIntro.class);
  private static AtomicInteger idCounter = new AtomicInteger(0);

  Supplier<Observer<String>> gimme = () -> {
    return new Observer<String>() {
      @Override
      public void onSubscribe(Disposable d) {
        logger.info("OBSERVER: Subscribed");
      }

      @Override
      public void onNext(String s) {
        logger.info("OBSERVER: ON NEXT: {}", s);
      }

      @Override
      public void onError(Throwable e) {
        logger.error("OBSERVER: ERROR: ", e);
      }

      @Override
      public void onComplete() {
        logger.info("OBSERVER: completed");
      }
    };
  };

  @Test
  public void justCreateObservable() {
    Observable<String> first = Observable.just("Hello");

    Observable<String> second = Observable.<String>create(observableEmitter -> {
      logger.info("Creating a");
      observableEmitter.onNext("a");
      logger.info("Creating b");
      observableEmitter.onNext("b");
      logger.info("Creating c");
      observableEmitter.onNext("c");
      observableEmitter.onComplete();
    });

    logger.info("Calling second observable");
    second.subscribe(gimme.get());
  }

  ;

  @Test
  public void justCreateObservable2() {
    Observable<String> second = Observable.<String>create(observableEmitter -> {
      logger.info("Creating a");
      observableEmitter.onNext("a");
      logger.info("Creating b");
      observableEmitter.onNext("b");
      logger.info("Creating c");
      observableEmitter.onNext("c");
      observableEmitter.onComplete();
    });

    Disposable disp = second
        .doOnSubscribe(disposable -> logger.info("Subscribed"))
        .doOnNext(s -> logger.info("ON NEXT: {}", s))
        .doOnError(throwable -> logger.error("ERROR: ", throwable))
        .doOnComplete(() -> logger.info("completed"))
        .doOnDispose(() -> logger.info("disposed"))
        .subscribe();

    disp.dispose();
  }

  // the exception is not handled
  @Test
  public void justCreateObservable3() {
    Observable<String> second = Observable.<String>create(observableEmitter -> {
      logger.info("Creating a");
      observableEmitter.onNext("a");
      logger.info("Creating b");
      observableEmitter.onNext("b");
      logger.info("About to err");
      observableEmitter.onError(new RuntimeException("BOOOOM"));
      observableEmitter.onComplete();
    });

    Disposable disp = second
        .doOnSubscribe(disposable -> logger.info("Subscribed"))
        .doOnNext(s -> logger.info("ON NEXT: {}", s))
        .doOnError(throwable -> logger.error("ERROR: ", throwable))
        .doOnComplete(() -> logger.info("completed"))
        .doOnDispose(() -> logger.info("disposed"))
        .subscribe();

    disp.dispose();
  }

  // the exception is not handled
  @Test
  public void justCreateObservable4() {
    Observable<String> second = Observable.<String>create(observableEmitter -> {
      logger.info("Creating a");
      observableEmitter.onNext("a");
      logger.info("Creating b");
      observableEmitter.onNext("b");
      logger.info("About to err");
      observableEmitter.onError(new RuntimeException("BOOOOM"));
      observableEmitter.onComplete();
    });

    Disposable disp = second
        .doOnSubscribe(disposable -> logger.info("Subscribed"))
        .doOnNext(s -> logger.info("ON NEXT: {}", s))
        .doOnComplete(() -> logger.info("completed"))
        .doOnDispose(() -> logger.info("disposed"))
        .subscribe();

    disp.dispose();
  }

  // the exception is not handled
  @Test
  public void justCreateObservable5() {
    Observable<String> second = Observable.<String>create(observableEmitter -> {
      logger.info("Creating a");
      observableEmitter.onNext("a");
      logger.info("Creating b");
      observableEmitter.onNext("b");
      logger.info("About to err");
      observableEmitter.onError(new RuntimeException("BOOOOM"));
      observableEmitter.onComplete();
    });

    Disposable disp = second
        .doOnSubscribe(disposable -> logger.info("Subscribed"))
        .doOnNext(s -> logger.info("ON NEXT: {}", s))
        .doOnComplete(() -> logger.info("completed"))
        .doOnDispose(() -> logger.info("disposed"))
        .subscribe();

    disp.dispose();
  }

  @Test
  public void delayedExecution() throws InterruptedException {
    MutableObject<Thread> thread = new MutableObject<Thread>();

    Observable<String> lazy = Observable.<String>create(emitter -> {
      logger.info("[IN CREATE] oh, hai");
      Thread t = new Thread(() -> {
        try {
          Thread.sleep(1000);
          logger.info("[IN CREATE] a");
          emitter.onNext("a");
          Thread.sleep(1000);
          logger.info("[IN CREATE] b");
          emitter.onNext("b");
          Thread.sleep(1000);
          logger.info("[IN CREATE] c");
          emitter.onNext("c");
        } catch (InterruptedException e) {
          logger.info("KaBoom", e);
        }
      });
      thread.setValue(t);
      t.start();
      logger.info("[IN CREATE] CREATE FINISHED");
    });

    logger.info("Gonna subscribe...");
    Thread.sleep(5000);

    // execute the observable
    lazy.subscribe(gimme.get());

    thread.getValue().join();
    logger.info("LAST LINE...");
  }

  // what happens if observer creates an exception?
  @Test
  public void justCreateObservableAndObserverCreatesAnException() {
    Observable<String> second = Observable.<String>create(observableEmitter -> {
      logger.info("Creating a");
      observableEmitter.onNext("a");
      logger.info("Creating b");
      observableEmitter.onNext("b");
      logger.info("Creating c");
      observableEmitter.onNext("c");
      observableEmitter.onComplete();
    });

    Observable result = second
        .map(String::toUpperCase)
        .map(s -> {
          if ("b".equalsIgnoreCase(s)) {
            throw new RuntimeException("B");
          }
          return s;
        });

    result.subscribe(gimme.get());
  }

  // what happens if observer creates an exception?
  @Test
  public void justCreateObservableAndObserverCreatesAnException1() {
    Observable<String> second = Observable.<String>create(observableEmitter -> {
      logger.info("Creating a");
      observableEmitter.onNext("a");
      logger.info("Creating b");
      observableEmitter.onNext("b");
      logger.info("Creating c");
      observableEmitter.onNext("c");
      observableEmitter.onComplete();
      //observableEmitter.setDisposable();
    });

    Observable result = second
        .map(String::toUpperCase)
        .map(s -> {
          if ("b".equalsIgnoreCase(s)) {
            throw new RuntimeException("B");
          }
          return s;
        })
        .doOnError(throwable -> logger.info("1: Error happened! {}", throwable.getMessage()))
        .onErrorReturn(throwable -> "swallowed")
        .doOnError(throwable -> logger.info("2: Error happened! {}", throwable.getMessage()));

    result.subscribe(gimme.get());

    //result.unsubscribeOn()
  }

  @Test
  public void fromLetter() {
    Observable<String> src = Observable.just(identify("TEST"), identify("TEST"), identify("TEST"));

    src
        .take(1)
        .doOnNext(logger::info)
        .subscribe();

    src
        .take(2)
        .doOnNext(logger::info)
        .subscribe();
  }

  @Test
  public void fromLetterCallable() {

    Observable<String> src = Observable.fromCallable(() -> identify("TEST")).repeat();

    src
        .take(3)
        .doOnNext(logger::info)
        .subscribe();

    src
        .take(3)
        .doOnNext(logger::info)
        .subscribe();
  }

  private static String identify(String id) {
    String result = id + idCounter.incrementAndGet();
    logger.info("Creating {}", result);
    return result;
  }

  @Test
  public void mergeEmpty() {
    Observable.merge(Observable.just(1), Observable.empty(), Observable.just(3), Observable.just(4))
        .subscribe(next -> logger.info("There is some output: {}", next));
  }

  @Test
  public void mergeNever() {
    Observable.merge(Observable.just(1), Observable.never(), Observable.just(3), Observable.just(4))
        .subscribe(next -> logger.info("There is some output: {}", next));
  }

  @Test
  public void mergeError() {
    Observable.merge(Observable.just(1), Observable.error(new RuntimeException("KaBoom")), Observable.just(3), Observable.just(4))
        .onErrorResumeNext(Observable.just(2))
        .subscribe(next -> logger.info("There is some output: {}", next));
  }

  @Test
  public void maybeToObservable() {
    Maybe.just(1)
        .subscribe(next -> logger.info("There is some output: {}", next), throwable -> {}, () -> logger.info("END"));
  }

  @Test
  public void completableToObservable() {
    Completable.complete()
        .toObservable()
        .subscribe(next -> logger.info("There is some output: {}", next), throwable -> {}, () -> logger.info("END"));
  }

}
