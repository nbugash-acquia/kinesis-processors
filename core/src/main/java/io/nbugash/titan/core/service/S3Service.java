package io.nbugash.titan.core.service;

import io.nbugash.titan.core.model.Capture;

public interface S3Service {
  void setS3Bucket(String s3Bucket);
  String uploadCaptureToS3(Capture capture);
  void tagS3Key(String s3Key, String tagKey, String tagValue);
  void setRetentionPeriod(String s3Key, Integer minutes);
}
