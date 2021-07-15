package com.github.nenomm.marbles.diy.ohhai;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OhHaiRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(OhHaiRunner.class);

  @Test
  public void testRun() throws InterruptedException {
    Observable test = Observable.from(emitter -> {
      emitter.emit("one");
      emitter.emit("two");
      emitter.emit("three");
      emitter.emit("four");
    });

    test
        .next(new Map(String::toUpperCase))
        .subscribe(LOGGER::info);

    test
        .next(new Map(s -> "another" + s))
        .next(new Map(String::toUpperCase))
        .subscribe(LOGGER::info);

    EventLoop.getEventLoop().shutdown();
  }
}