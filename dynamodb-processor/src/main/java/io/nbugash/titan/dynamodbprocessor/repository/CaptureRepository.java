package io.nbugash.titan.dynamodbprocessor.repository;

import io.nbugash.titan.core.model.Capture;
import io.nbugash.titan.core.utility.CaptureMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemResponse;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CaptureRepository {

  private final DynamoDbAsyncClient dynamoDbAsyncClient;

  @Value("${application.dynamodb.table.name:captures}")
  private String tableName;

  public Flux<Capture> getCaptures() {
    log.debug("Creating scan request");
    final ScanRequest scanRequest = ScanRequest.builder()
        .tableName(tableName)
        .build();
    log.debug("Scan request created");
    log.debug("Retrieving results from dynamodb");
    return Mono.fromCompletionStage(dynamoDbAsyncClient.scan(scanRequest))
        .map(ScanResponse::items)
        .map(CaptureMapper::fromListMapToCaptureList)
        .flatMapMany(Flux::fromIterable)
        ;
  }

  public Mono<Capture> getCapture(Long captureId) {
    log.debug("Creating get item request");
    final GetItemRequest getItemRequest = GetItemRequest.builder()
        .tableName(tableName)
        .key(Map.of("id", AttributeValue.builder().n(String.valueOf(captureId)).build()))
        .build();
    log.debug("Get request item created");
    log.debug("Retrieving item from dynamodb");
    return Mono.fromCompletionStage(dynamoDbAsyncClient.getItem(getItemRequest))
        .map(GetItemResponse::item)
        .map(CaptureMapper::fromMapToCapture);

  }

  public Mono<Capture> saveCapture(Capture capture) {
    log.debug("Creating put item request for table: " + tableName);
    final PutItemRequest putItemRequest = PutItemRequest.builder().tableName(tableName)
        .item(CaptureMapper.fromCaptureToMap(capture)).build();
    log.debug("Saving capture " + capture.getId() + " from " + capture.getAccountId() + " to dynamodb");
    return Mono.fromCompletionStage(dynamoDbAsyncClient.putItem(putItemRequest))
        .map(putItemResponse -> CaptureMapper.fromMapToCapture(putItemResponse.attributes()));
  }

  public Mono<Capture> updateCapture(Long captureId, Capture capture) {
    capture.setId(captureId);
    return saveCapture(capture);
  }

  public Mono<Capture> deleteCapture(Long captureId) {
    log.debug("Creating delete item request");
    final DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder()
        .key(Map.of("id", AttributeValue.builder().n(String.valueOf(captureId)).build()))
        .build();
    log.debug("Delete item request created");
    log.debug("Deleting capture :" + captureId + " from dynamodb");
    return Mono.fromCompletionStage(dynamoDbAsyncClient.deleteItem(deleteItemRequest))
        .map(DeleteItemResponse::attributes)
        .map(CaptureMapper::fromMapToCapture)
        ;
  }
}
