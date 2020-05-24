package io.nbugash.titan.api;

import io.nbugash.titan.core.model.Capture;
import java.util.Random;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@EnableBinding(Source.class)
@RequiredArgsConstructor
@Slf4j
@SpringBootApplication
public class ApiApplication {

  private final Source source;

  public static void main(String[] args) {
    SpringApplication.run(ApiApplication.class, args);
  }

  @Scheduled(fixedRate = 3000L)
  public void sendCapture() {
    Capture capture = Capture.builder()
        .id(System.currentTimeMillis())
        .accountId(accountGenerator())
        .data(UUID.randomUUID().toString())
        .build();
    source.output().send(MessageBuilder.withPayload(capture).build());
    log.debug("Sent some capture: " + capture.getId() + " for " + capture.getAccountId());
  }

  private String accountGenerator() {
    String[] accountList = {"AMD", "PFIZER", "STAPLES"};
    return accountList[Math.abs(new Random().nextInt()) % accountList.length];
  }
}
