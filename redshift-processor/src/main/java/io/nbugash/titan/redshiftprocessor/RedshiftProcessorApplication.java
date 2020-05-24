package io.nbugash.titan.redshiftprocessor;

import io.nbugash.titan.core.model.Status;
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
import org.springframework.messaging.handler.annotation.Payload;

@Slf4j
@EnableBinding(Sink.class)
@RequiredArgsConstructor
@SpringBootApplication
public class RedshiftProcessorApplication {

  private final String STATUS_TAG = "STATUS";

  @Value("${cloud.aws.s3.bucket.name}")
  private String s3BucketName;

  private final S3Service s3Service;

  public static void main(String[] args) {
    SpringApplication.run(RedshiftProcessorApplication.class, args);
  }

  @PostConstruct
  private void initialize() {
    this.s3Service.setS3Bucket(s3BucketName);
  }


  @StreamListener(Sink.INPUT)
  public void loadCaptureToRedshiftAndTagProcessed(@Payload String s3Key) {
    Boolean isLoadSuccess = loadCaptureToRedshift(s3Key);
    if (isLoadSuccess) {
      s3Service.tagS3Key(s3Key, STATUS_TAG, String.valueOf(Status.PROCESSED));
      s3Service.setRetentionPeriod(s3Key, 30);
    }
  }

  private Boolean loadCaptureToRedshift(String s3Key) {
    log.debug("Loading " + s3Key + " to redshift");
    try {
      log.debug("Simulating a long running job");
      Thread.sleep(3000L);
    } catch (InterruptedException exception) {
      log.error("Interrupted the redshift loading process");
      return false;
    }
    return true;
  }

}
