package io.nbugash.titan.dynamodbprocessor.configuration;

import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@Slf4j
@Configuration
public class DynamoDBConfig {

  @Value("${application.dynamodb.endpoint}")
  private String dynamodbEndpoint;

  @Value("${cloud.aws.credentials.profile-name}")
  private String awsProfileName;

  @Value("${cloud.aws.region.static}")
  private String awsRegion;

  @Bean
  public DynamoDbAsyncClient dynamodbAsyncClient() {
    log.debug("Creating a dynamodb async client bean");
    return DynamoDbAsyncClient.builder()
        .endpointOverride(URI.create(dynamodbEndpoint))
        .credentialsProvider(DefaultCredentialsProvider.builder().profileName(awsProfileName).build())
        .region(Region.of(awsRegion))
        .build();
  }
}
