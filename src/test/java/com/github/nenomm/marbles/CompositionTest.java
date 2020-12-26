package com.github.nenomm.marbles;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// composing existing operators together
public class CompositionTest {

  private static final Logger logger = LoggerFactory.getLogger(CompositionTest.class);

  @Test
  public void fromLetter() throws InterruptedException {
    Observable
        .just("A", "B", "C")
        .repeat()
        .take(20)
        .compose(everyThird() )
        .doOnNext(string -> logger.info("{}", string))
        .subscribe();

    Thread.sleep(5);
  }

  ObservableTransformer<String, String> everyThird() {
    var three = Observable.just(1,2,3).repeat();

    return upstream -> {
      return Observable.zip(upstream, three, (s, integer) -> Pair.of(s, integer))
          .filter(stringIntegerPair -> stringIntegerPair.getRight() % 3 == 0)
          .map(stringIntegerPair -> stringIntegerPair.getLeft());
    };
  }
}
