package io.nbugash.titan.s3processor;

import io.nbugash.titan.core.model.Capture;
import io.nbugash.titan.core.service.S3Service;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;

@Slf4j
@EnableBinding({Sink.class,Source.class})
@RequiredArgsConstructor
@SpringBootApplication
public class S3ProcessorApplication {

  @Value("${cloud.aws.s3.bucket.name}")
  private String s3BucketName;

  private final S3Service s3Service;
  private final Source source;

  public static void main(String[] args) {
    SpringApplication.run(S3ProcessorApplication.class, args);
  }

  @PostConstruct
  public void initialize() {
    this.s3Service.setS3Bucket(s3BucketName);
  }

  @StreamListener(Sink.INPUT)
  public void saveToS3AndSendDownstream(@Payload Capture capture){
    String s3Key = saveToS3(capture);
    sendDownstream(s3Key);
  }

  private void sendDownstream(String s3Key) {
    log.debug("Sending s3key: " + s3Key + " downstream");
    source.output().send(MessageBuilder.withPayload(s3Key).build());
  }

  private String saveToS3(Capture capture, String... tags) {
    log.debug("Saving capture: " + capture.getId() + " to S3");
    return s3Service.uploadCaptureToS3(capture);
  }

}
