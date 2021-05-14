package com.github.nenomm.marbles.diy.ohhai;

import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Observable {
  private static final Logger LOGGER = LoggerFactory.getLogger(Observable.class);

  private Observer observer;
  final private Source source;

  private Observable(Source source) {
    this.source = source;
  }

  public static Observable from(Source source) {
    return new Observable(source);
  }

  public void subscribe(Consumer<String> consumer) {
    LOGGER.debug("Subscribing...");
    source.start(EmitterFactory.create(this, consumer));
  }

  public Observer getObserver() {
    return observer;
  }

  public Observable next(Observer next) {
    if (observer == null) {
      observer = next;
    } else {
      Observer o = observer;
      while (o.getNext() != null) {
        o = o.getNext();
      }
      o.setNext(next);
    }
    return this;
  }

  public void clear() {
   observer = null;
  }
}
