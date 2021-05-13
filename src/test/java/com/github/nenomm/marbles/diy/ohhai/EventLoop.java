package com.github.nenomm.marbles.diy.ohhai;

import java.lang.Thread.State;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventLoop {

  private static final Logger LOGGER = LoggerFactory.getLogger(EventLoop.class);
  private static final EventLoop INSTANCE = new EventLoop();

  private final ConcurrentLinkedDeque<Visitor> visitors = new ConcurrentLinkedDeque<>();
  private final Thread eventLoop = new Thread(this::run);

  private volatile boolean shutdown = false;

  private EventLoop() {
    eventLoop.start();
  }

  public static EventLoop getEventLoop() {
    return INSTANCE;
  }


  public synchronized void submit(Visitor visitor) {
    LOGGER.debug("Submitting new visitor");
    visitors.offer(visitor);
    notify();
  }

  private synchronized void run() {
    while (!visitors.isEmpty() || !shutdown) {
      Visitor visitor = visitors.poll();
      if ((visitor != null) && visitor.visit()) {
        visitors.offer(visitor);
      } else if (visitor != null) {
        visitor.finish();
      }

      if (visitors.isEmpty() && !shutdown) {
        try {
          LOGGER.debug("Suspending the event loop");
          wait();
        } catch (InterruptedException e) {
          LOGGER.debug("Resuming the event loop");
        }
      }

    }
    LOGGER.debug("Event loop exit");
  }

  public void shutdown() throws InterruptedException {
    shutdown = true;
    if (eventLoop.getState() == State.WAITING) {
      LOGGER.debug("Shutdown - toggling flag");
      notify();
    } else {
      LOGGER.debug("Shutdown - join");
    }
    eventLoop.join();
  }
}
