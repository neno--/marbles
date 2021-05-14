package com.github.nenomm.marbles.diy.ohhai;

import java.lang.Thread.State;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventLoop {

  private static final Logger LOGGER = LoggerFactory.getLogger(EventLoop.class);
  private static final EventLoop INSTANCE = new EventLoop();

  private final ConcurrentLinkedQueue<Visitor> visitors = new ConcurrentLinkedQueue<>();
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
    if (eventLoop.getState() == State.WAITING) {
      notify();
    }
  }

  private void run() {
    while (!visitors.isEmpty() || !shutdown) {

      synchronized (this) {
        Visitor visitor = visitors.poll();
        if ((visitor != null) && visitor.visit()) {
          LOGGER.debug("OFFERING: {}", visitors.size());
          visitors.offer(visitor);
        } else if (visitor != null) {
          visitor.finish();
          LOGGER.debug("QUEUE: {}", visitors.size());
        }
      }

      if (visitors.isEmpty() && !shutdown) {
        try {
          synchronized (this) {
            wait();
          }
        } catch (InterruptedException e) {
          LOGGER.debug("Resuming the event loop");
        }
      }

    }
    LOGGER.debug("Event loop exit");
  }

  public void shutdown() throws InterruptedException {
    shutdown = true;
    synchronized (this) {
      if (eventLoop.getState() == State.WAITING) {
        LOGGER.debug("Shutdown - toggling flag");
        notify();
      } else {
        LOGGER.debug("Shutdown - join");
      }
    }
    eventLoop.join();
  }
}
