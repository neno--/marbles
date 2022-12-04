package com.github.nenomm.reactor.web;


import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class WebClientIntro {

  private static final Logger logger = LoggerFactory.getLogger(WebClientIntro.class);

  @Test
  public void justCreateMono() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);

    WebClient.create("https://httpstat.us").get()
        //.uri("/200") // this one has a body
        .uri("/204") // body is not present
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .toEntity(String.class)
        .filter(stringResponseEntity -> stringResponseEntity.hasBody())
        // prevent further propagation if this mono is empty
        .switchIfEmpty(Mono.just(ResponseEntity.of(Optional.of("I am empty"))))
        .subscribe(stringResponseEntity -> {
          logger.info("There it is: {}", stringResponseEntity.getBody());
          latch.countDown();
        });

    // network is slow, wait for a while
    latch.await();
  }
}
