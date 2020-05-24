package io.nbugash.titan.core.utility;

import io.nbugash.titan.core.model.Capture;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class S3Util {

  @Value("${cloud.aws.s3.datetime-pattern:yyyy-MM-dd}")
  private String dateTimePattern;

  /**
   * Given a capture this function will return the s3key
   * @param Capture
   * @return String
   */
  public String getS3KeyFromCapture(Capture capture) {
    String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern(dateTimePattern));
    return String.format("%s/%s/%s.json", currentDate, capture.getAccountId(), capture.getId());
  }
}
