package io.nbugash.titan.dynamodbprocessor;

import io.nbugash.titan.core.model.Capture;
import io.nbugash.titan.dynamodbprocessor.repository.CaptureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;

@Slf4j
@RequiredArgsConstructor
@EnableBinding({Sink.class, Source.class})
@SpringBootApplication
public class DynamodbProcessorApplication {

  private final CaptureRepository repository;
  private final Source source;

  public static void main(String[] args) {
    SpringApplication.run(DynamodbProcessorApplication.class, args);
  }

  @StreamListener(Sink.INPUT)
  public void saveCaptureAndPushDownstream(@Payload Capture capture) {
    saveCapture(capture);
    pushDownstream(capture);
  }

  private void pushDownstream(Capture capture) {
    log.debug("Sending capture: " + capture.getId() + " downstream");
    source.output().send(MessageBuilder.withPayload(capture).build());
  }

  private void saveCapture(Capture capture) {
    repository.saveCapture(capture);
  }

}
