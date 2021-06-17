package com.github.nenomm.marbles;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// composing existing operators together
public class AnotherCompositionTest {

  private static final Logger logger = LoggerFactory.getLogger(AnotherCompositionTest.class);
  private static AtomicInteger idCounter = new AtomicInteger(0);

  @Test
  public void fromLetter() {
    AtomicInteger counter = new AtomicInteger(0);
    Observable
        //.just("A", "B", "C", "D", "E", "F", "G", "H")
        //.just(identify("A"), identify("B"))
        .just(identify("A"), identify("B"), identify("C"))
        .take(1)
        .doOnNext(s -> logger.info("Before compose: {} for {}", counter.incrementAndGet(), s))
        .map(s -> String.format("outer(%s)", s))
        .compose(new Source())
        .doOnNext(s -> logger.info("After compose: {} for {}", counter.get(), s))
        .subscribe(s -> logger.info("Consuming {}/{}", s, counter.get()));
  }

  private static class Source implements ObservableTransformer<String, String> {

    @NonNull
    @Override
    public ObservableSource<String> apply(@NonNull Observable<String> upstream) {
      logger.info("Executing apply");
      return upstream
          .map(s -> String.format("applied(%s)", s))
          .doOnNext(s -> logger.info("  Doing some work for {}", s))
          .map(String::toUpperCase)
          .compose(upstream1 -> upstream.doOnNext(s1 -> logger.info("  What a mess for {}", s1)));
    }
  }

  @Test
  public void fromLetterFlattened() {
    AtomicInteger counter = new AtomicInteger(0);
    Observable
        .just(identify("A"), identify("B"), identify("C"))
        .take(2)
        .doOnNext(s -> logger.info("Before compose: {} for {}", counter.incrementAndGet(), s))
        .compose(new Source2())
        .doOnNext(s -> logger.info("After compose: {} for {}", counter.get(), s))
        .subscribe(s -> logger.info("Consuming {}/{}", s, counter.get()));
  }

  private static class Source2 implements ObservableTransformer<String, String> {

    @NonNull
    @Override
    public ObservableSource<String> apply(@NonNull Observable<String> upstream) {
      logger.info("Executing apply");
      return upstream
          .map(s -> String.format("applied(%s)", s))
          .doOnNext(s -> logger.info("  Doing some work for {}", s))
          .map(String::toUpperCase)
          .flatMap(s -> {
            logger.info("  Executing magic for {}", s);
            return upstream.doOnNext(s1 -> logger.info("  What a mess for {}", s1));
          })
          .doOnNext(s -> logger.info("  End of mess for {}", s));
    }
  }

  private static String identify(String id) {
    String result = id + idCounter.incrementAndGet();
    logger.info("Creating {}", result);
    return result;
  }

  @Test
  public void fromLetterFlattenedCallable() {
    AtomicInteger counter = new AtomicInteger(0);
    Observable.fromCallable(() -> identify("TEST"))
        .doOnNext(s -> logger.info("Before compose: {} for {}", counter.incrementAndGet(), s))
        .compose(new Source2())
        .doOnNext(s -> logger.info("After compose: {} for {}", counter.get(), s))
        .subscribe(s -> logger.info("Consuming {}/{}", s, counter.get()));
  }

  private static class Source3 implements ObservableTransformer<String, String> {

    @NonNull
    @Override
    public ObservableSource<String> apply(@NonNull Observable<String> upstream) {
      logger.info("Executing apply");
      return upstream
          .map(s -> String.format("applied(%s)", s))
          .doOnNext(s -> logger.info("  Doing some work for {}", s))
          .map(String::toUpperCase)
          .flatMap(s -> {
            logger.info("  Executing magic for {}", s);
            return upstream.doOnNext(s1 -> logger.info("  What a mess for {}", s1));
          })
          .doOnNext(s -> logger.info("  End of mess for {}", s));
    }
  }
}
