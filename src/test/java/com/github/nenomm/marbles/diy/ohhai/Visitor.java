package com.github.nenomm.marbles.diy.ohhai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Visitor {
  private static final Logger LOGGER = LoggerFactory.getLogger(Visitor.class);

  String input;
  Observable owner;
  Observer observer;

  public Visitor(String input, Observable owner) {
    this.input = input;
    this.owner = owner;
    this.observer = owner.getObserver();
  }

  public void run() {
    EventLoop.getEventLoop().submit(this);
  }

  public boolean visit() {
    LOGGER.debug("Visiting...");
    input = observer.execute(input);
    observer = observer.getNext();
    return observer != null;
  }

  public void finish() {
    LOGGER.debug("Finished");
    owner.finish(input);
  }
}
