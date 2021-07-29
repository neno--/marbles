package com.github.nenomm.marbles;

import io.reactivex.Observable;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScannerDarkly {

  private static final Logger logger = LoggerFactory.getLogger(ScannerDarkly.class);

  @Test
  public void justCreateScan() {
    Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
        .scan(-1, (acumulator, element) -> element)
        .doOnNext(integer -> logger.info("Next is {}", integer))
        .subscribe();
  }

  @Test
  public void justCreateScanWithoutInitialElement() {
    Observable<Integer> integerObservable = Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
        .scan((acumulator, element) -> element)
        .doOnNext(integer -> logger.info("Next is {}", integer));

    integerObservable.take(1).subscribe();
    integerObservable.take(1).subscribe();
    integerObservable.take(1).subscribe();
  }
}
