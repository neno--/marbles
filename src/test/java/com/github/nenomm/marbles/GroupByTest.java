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

public class GroupByTest {

  private static final Logger logger = LoggerFactory.getLogger(GroupByTest.class);

  @Test
  public void simpleGroupBy() {
    Observable.just(1,2,3,4,5,6,7,8,9,10)
        .groupBy(integer -> integer % 2)
        .map(integerIntegerGroupedObservable -> integerIntegerGroupedObservable.map(integer -> integer* 10))
        .subscribe(integerIntegerGroupedObservable -> logger.info("HEH? {}", integerIntegerGroupedObservable.subscribe(integer -> logger.info("Eto ga: {}",integer))));
  }

}
