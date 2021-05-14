package com.github.nenomm.marbles.diy.ohhai;

import com.github.nenomm.marbles.diy.ohhai.EmitterFactory.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Visitor {

  private static final Logger LOGGER = LoggerFactory.getLogger(Visitor.class);

  private String source;
  private String input;
  private Observer observer;
  private Emitter owner;


  public Visitor(String input, Observable observable, Emitter owner) {
    this.source = input;
    this.input = input;
    this.observer = observable.getObserver();
    this.owner = owner;

  }

  public void run() {
    EventLoop.getEventLoop().submit(this);
  }

  public boolean visit() {
    LOGGER.debug("Before visit: {}/{}", source, input);
    input = observer.execute(input);
    LOGGER.debug("After visit: {}/{}", source, input);
    observer = observer.getNext();
    return observer != null;
  }

  public void finish() {
    LOGGER.debug("Finished for {} is {}", source, input);
    owner.finish(input);
  }
}
