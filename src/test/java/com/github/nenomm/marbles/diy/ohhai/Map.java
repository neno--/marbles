package com.github.nenomm.marbles.diy.ohhai;

import java.util.function.Function;

public class Map extends Observer {

  private Function<String, String> function;

  public Map(Function<String, String> function) {
    this.function = function;
  }

  @Override
  public String execute(String input) {
    return function.apply(input);
  }
}
