package com.github.nenomm.marbles.diy.ohhai;

import java.util.function.Consumer;

public class EmitterFactory {

  public static Emitter create(Observable owner, Consumer<String> consumer) {
    return new Emitter(owner, consumer);
  }

  public static class Emitter {

    private Observable owner;
    private Consumer<String> consumer;

    public Emitter(Observable owner, Consumer<String> consumer) {
      this.owner = owner;
      this.consumer = consumer;
    }

    public void emit(String input) {
      Visitor visitor = new Visitor(input, owner, this);
      visitor.run();
    }

    public void finish(String value) {
      owner.clear();
      consumer.accept(value);
    }
  }
}
