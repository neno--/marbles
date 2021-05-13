package com.github.nenomm.marbles.diy.ohhai;

public class Emitter {

  private Observable owner;

  public Emitter(Observable owner) {
    this.owner = owner;
  }

  public void emit(String input) {
    Visitor visitor = new Visitor(input, owner);
    visitor.run();
  }
}
