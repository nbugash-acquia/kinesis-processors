package io.nbugash.titan.core.service;

import com.google.gson.Gson;
import io.nbugash.titan.core.model.Capture;
import io.nbugash.titan.core.model.Status;
import io.nbugash.titan.core.utility.S3Util;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.ObjectLockRetention;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRetentionRequest;
import software.amazon.awssdk.services.s3.model.PutObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.services.s3.model.Tagging;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

  private static final String STATUS_TAG = "STATUS";

  @Value("${cloud.aws.region.static}")
  private String regionName;

  @Value("${cloud.aws.credentials.profile-name}")
  private String awsProfileName;

  private final S3Util s3Util;

  private S3AsyncClient s3Client;

  private String s3BucketName;

  private Gson gson;

  @PostConstruct
  public void initialize() {
    log.debug("Creating an async s3 client");
    s3Client = S3AsyncClient.builder()
        .region(Region.of(regionName))
        .credentialsProvider(DefaultCredentialsProvider.builder()
            .profileName(awsProfileName).build())
        .build();
    gson = new Gson();
  }

  @Override
  public void setS3Bucket(String s3Bucket) {
    this.s3BucketName = s3Bucket;
  }

  @Override
  public String uploadCaptureToS3(Capture capture) {
    String s3Key = s3Util.getS3KeyFromCapture(capture);
    log.debug("Uploading capture to " + String.format("s3://%s/%s", s3BucketName, s3Key));
    log.debug("Saving " + s3Key + " to s3");
    final PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(s3BucketName)
        .key(s3Key)
        .tagging(Tagging.builder()
            .tagSet(Tag.builder()
                .key(STATUS_TAG)
                .value(String.valueOf(Status.UNPROCESSED))
                .build())
            .build())
        .build();
    s3Client.putObject(putObjectRequest, AsyncRequestBody.fromString(gson.toJson(capture, Capture.class)));
    return s3Key;
  }

  @Override
  public void tagS3Key(String s3Key, String tagKey, String tagValue) {
    s3Client.putObjectTagging(PutObjectTaggingRequest.builder()
        .bucket(s3BucketName)
        .key(s3Key)
        .tagging(Tagging.builder()
            .tagSet(Tag.builder()
                .key(tagKey.toUpperCase())
                .value(tagValue.toUpperCase())
                .build())
            .build())
        .build());
  }

  @Override
  public void setRetentionPeriod(String s3Key, Integer minutes ) {
    s3Client.putObjectRetention(PutObjectRetentionRequest.builder()
        .bucket(s3BucketName)
        .key(s3Key)
        .retention(ObjectLockRetention.builder()
            .retainUntilDate(Instant.now().plus(minutes, ChronoUnit.MINUTES))
            .build())
        .build());
  }
}
