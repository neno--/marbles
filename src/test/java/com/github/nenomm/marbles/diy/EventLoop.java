package com.github.nenomm.marbles.diy;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventLoop {

  private static final Logger logger = LoggerFactory.getLogger(EventLoop.class);

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

  public EventLoop on(String key, Consumer<Object> handler) {
    handlers.put(key, handler);
    return this;
  }

  public void dispatch(Event event) {
    events.add(event);
  }

  public void stop() {
    Thread.currentThread().interrupt();
  }

  public void run() {
    while (!events.isEmpty() || !Thread.interrupted()) {
      if (!events.isEmpty()) {
        Event event = events.pop();
        if (handlers.containsKey(event.getKey())) {
          handlers.get(event.getKey()).accept(event.data);
        } else {
          logger.error("No handler for key {}", event.getKey());
        }
      }
    }
  }
}
