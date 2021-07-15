package com.github.nenomm.vertx;


import io.reactivex.Single;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(VertxExtension.class)
public class VertxTest {

  private static final Logger logger = LoggerFactory.getLogger(VertxTest.class);
  private Vertx vertx;

  @BeforeEach
  void deployVerticle(VertxTestContext testContext) {
    this.vertx = Vertx.vertx();
    this.vertx.deployVerticle(new TestVerticle(testContext), testContext.succeeding(id -> testContext.completeNow()));
  }

  public static class TestVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestVerticle.class);
    private VertxTestContext vertxTestContext;


    public TestVerticle(VertxTestContext vertxTestContext) {
      this.vertxTestContext = vertxTestContext;
    }

    @Override
    public void start() {
      vertx.eventBus().consumer("HELLO", this::handle);
    }

    private void handle(Message request) {
      final long delay = new Random().nextInt(1000);

      Single
          .just("OK")
          .doOnSuccess(s -> LOGGER.info("Delaying for {} ms.", delay))
          .delay(delay, TimeUnit.MILLISECONDS)
          .subscribe(reply -> request.reply(reply));
    }
  }

  @Test
  void testVertx(VertxTestContext testContext) {
    final Checkpoint checkpoint = testContext.checkpoint();
    DeliveryOptions fastFail = new DeliveryOptions();
    fastFail.setSendTimeout(300);

    this.vertx
        .rxDeployVerticle(new TestVerticle(testContext))
        .flatMap(success -> this.vertx.eventBus()
            .rxRequest("HELLO", "Hi there", fastFail)
            .doOnError(throwable -> logger.error("Error, going to retry: {}", throwable.getMessage()))
            .retry(2)
            .onErrorResumeNext(throwable -> {
              logger.error("Fatal error while greeting: {}", throwable.getMessage());
              return Single.never();
            })
        )
        .subscribe(response -> testContext.verify(() -> {
          checkpoint.flag();
        }), testContext::failNow);
  }
}
