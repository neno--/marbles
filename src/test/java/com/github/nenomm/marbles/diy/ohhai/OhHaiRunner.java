package com.github.nenomm.marbles.diy.ohhai;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OhHaiRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(OhHaiRunner.class);

  @Test
  public void testRun() throws InterruptedException {
    Observable test = Observable.from(emitter -> {
      emitter.emit("1");
      emitter.emit("2");
      emitter.emit("3");
      emitter.emit("4");
    });

    test
        //.next(new Map(String::toUpperCase))
        .next(new Map(s -> "FIRST"))
        .subscribe(LOGGER::info);

    test
        .next(new Map(s -> "SECOND"))
        .subscribe(LOGGER::info);




    EventLoop.getEventLoop().shutdown();
  }
}