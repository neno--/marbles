package com.github.nenomm.marbles.diy.complex;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventLoopTesting {

  private static final Logger logger = LoggerFactory.getLogger(EventLoopTesting.class);

  public static void main(String[] args) {
    EventLoop eventLoop = EventLoop.getEventLoop();

    new Thread(() -> {
      for (int n = 0; n < 6; n++) {
        delay(1000);
        eventLoop.dispatch(new EventLoop.Event("tick", n));
      }
      eventLoop.dispatch(new EventLoop.Event("stop", null));
    }).start();

    eventLoop.on("tick", event -> {
      logger.info("Handling tick: {}", event);
    });

    //eventLoop.stop();
  }

  private static void delay(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
