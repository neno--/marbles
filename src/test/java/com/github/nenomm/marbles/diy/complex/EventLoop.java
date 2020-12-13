package com.github.nenomm.marbles.diy.complex;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventLoop {

  private static final Logger logger = LoggerFactory.getLogger(EventLoop.class);
  private static final EventLoop INSTANCE = new EventLoop();

  public static final class Event {

    private final String key;
    private final Object data;

    public Event(String key, Object data) {
      this.key = key;
      this.data = data;
    }

    public String getKey() {
      return key;
    }

    public Object getData() {
      return data;
    }
  }

  private final ConcurrentLinkedDeque<Event> events = new ConcurrentLinkedDeque<>();
  private final ConcurrentHashMap<String, Consumer<Object>> handlers = new ConcurrentHashMap<>();
  private final Thread eventLoop = new Thread(this::run);
  private volatile boolean running = true;

  private EventLoop() {
    eventLoop.start();
  }

  public static EventLoop getEventLoop() {
    return INSTANCE;
  }

  public EventLoop on(String key, Consumer<Object> handler) {
    handlers.put(key, handler);
    return this;
  }

  public synchronized void dispatch(Event event) {
    events.add(event);
    notify();
  }

  public void stop() {
    logger.info("Attempting to stop the event loop...");
    running = false;
  }

  private synchronized void run() {
    while (running) {
      while (!events.isEmpty()) {
        Event event = events.pop();
        if (handlers.containsKey(event.getKey())) {
          handlers.get(event.getKey()).accept(event.data);
        } else {
          logger.error("No handler for key {}", event.getKey());
        }
      }
      try {
        logger.info("Suspending the event loop");
        wait();
      } catch (InterruptedException e) {
        logger.info("Resuming the event loop");
      }
    }
    logger.info("Event loop shut down");
  }
}
