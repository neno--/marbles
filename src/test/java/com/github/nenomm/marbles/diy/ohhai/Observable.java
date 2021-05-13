package com.github.nenomm.marbles.diy.ohhai;

import java.util.function.Consumer;

public class Observable {

  private Observer observer;
  final private Source source;
  final private Emitter emitter;
  private Consumer<String> consumer;

  private Observable(Source source) {
    this.source = source;
    this.emitter = new Emitter(this);
  }

  public static Observable from(Source source) {
    return new Observable(source);
  }

  public void subscribe(Consumer<String> consumer) {
    this.consumer = consumer;
    source.start(emitter);

  }

  public void finish(String result) {
    consumer.accept(result);
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
}
