package com.github.nenomm.marbles.diy.ohhai;

public abstract class Observer {
  private Observer next;

  public abstract String execute(String value);

  public Observer getNext() {
    return next;
  }

  public void setNext(Observer next) {
    this.next = next;
  }
}
