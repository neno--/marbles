package com.github.nenomm.marbles.diy.ohhai;

import com.github.nenomm.marbles.diy.ohhai.EmitterFactory.Emitter;
import java.util.LinkedList;

public class Visitor {

  private String value;
  private LinkedList<Observable> list;
  private Emitter owner;

  public Visitor(String value, LinkedList<Observable> list, Emitter emitter) {
    this.value = value;
    this.list = list;
    this.owner = emitter;
  }

  public boolean execute() {
    value = list.removeLast().getObserver().execute(value);
    return !list.isEmpty();
  }

  public void finish() {
    owner.finish(value);
  }
}
