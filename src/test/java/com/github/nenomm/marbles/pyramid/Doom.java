package com.github.nenomm.marbles.pyramid;

import io.reactivex.Single;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Doom {

  private static final Logger logger = LoggerFactory.getLogger(Doom.class);


  public Single<Boolean> singleBoolean() {
    return Single.create(emitter -> {
      logger.info("Emitting boolean value...");
      emitter.onSuccess(true);
    });
  }

  public Single<Single<Boolean>> singleWrapped1() {
    return Single.create(emitter -> {
      logger.info("Emitting wrapped value...");
      emitter.onSuccess(singleBoolean());
    });
  }

  public Single<Single<Single<Boolean>>> singleWrapped2() {
    return Single.create(emitter -> {
      logger.info("Emitting wrapped wrapped value...");
      emitter.onSuccess(singleWrapped1());
    });
  }

  @Test
  public void test1() {
    singleWrapped2().subscribe(result -> {
      result.subscribe(booleanSingle -> {
        booleanSingle.subscribe(aBoolean -> {
        }, throwable -> {
        });
      }, throwable -> {
      });
    }, throwable -> {
    });
  }


}
