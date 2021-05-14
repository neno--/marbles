package com.github.nenomm.marbles.diy.ohhai;

import com.github.nenomm.marbles.diy.ohhai.EmitterFactory.Emitter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Observable {
  private static final Logger LOGGER = LoggerFactory.getLogger(Observable.class);

  private Observer observer;
  private Source source;
  private Observable parent;

  private Observable(Source source) {
    this.source = source;
  }

  private Observable(Observable parent, Observer observer) {
    this.parent = parent;
    this.observer = observer;
  }

  public static Observable from(Source source) {
    return new Observable(source);
  }

  private void subscribe(Consumer<String> consumer, LinkedList<Observable> list) {
    if (parent != null) {
      list.add(this);
      parent.subscribe(consumer, list);
    } else {
      LOGGER.debug("Subscribing...");
      source.start(EmitterFactory.create(list, consumer));
    }
  }

  public void subscribe(Consumer<String> consumer) {
    LinkedList<Observable> list = new LinkedList<>();
    list.add(this);
    if (parent != null) {
      parent.subscribe(consumer, list);
    } else {
      LOGGER.debug("Subscribing...");
      source.start(EmitterFactory.create(list, consumer));
    }
  }

  public Observer getObserver() {
    return observer;
  }

  public Observable next(Observer next) {
    return new Observable(this, next);
  }
}
