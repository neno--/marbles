package com.github.nenomm.marbles.diy.ohhai;

import java.util.LinkedList;
import java.util.function.Consumer;

public class EmitterFactory {

  public static Emitter create(LinkedList<Observable> list, Consumer<String> consumer) {
    return new Emitter(list, consumer);
  }

  public static class Emitter {

    LinkedList<Observable> list;
    private Consumer<String> consumer;

    public Emitter(LinkedList<Observable> list, Consumer<String> consumer) {
      this.list = list;
      this.consumer = consumer;
    }

    public void emit(String input) {
      EventLoop.getEventLoop().submit(new Visitor(input, new LinkedList<>(list), this));
    }

    public void finish(String value) {
      consumer.accept(value);
    }
  }
}
