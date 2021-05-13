package com.github.nenomm.marbles.diy.ohhai;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OhHaiRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(OhHaiRunner.class);

  @Test
  public void testRun() throws InterruptedException {
    Observable test = Observable.from(emitter -> {
      emitter.emit("Hello World");
      emitter.emit("One More");
    });

    test
        .next(new Map(String::toUpperCase))
        .next(new Map(s -> s.substring(s.length()/2)))
        .subscribe(LOGGER::info);

    test
        .next(new Map(String::toLowerCase))
        .subscribe(LOGGER::info);

    EventLoop.getEventLoop().shutdown();
  }
}