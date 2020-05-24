package io.nbugash.titan.core.utility;

import io.nbugash.titan.core.model.Capture;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class CaptureMapper {

  /**
   * Transform a map to a capture
   * @param stringAttributeValueMap
   * @return
   */
  public static Capture fromMapToCapture(Map<String, AttributeValue> stringAttributeValueMap) {
    if (stringAttributeValueMap.isEmpty()) throw new RuntimeException("Error: Trying to create a capture from an empty map");
    return Capture.builder()
        .id(Long.valueOf(stringAttributeValueMap.get("id").n()))
        .accountId(stringAttributeValueMap.get("accountId").s())
        .data(stringAttributeValueMap.get("data").s())
        .build();
  }

  /**
   * Transform a list of Map<String, AttributeValue> to list of captures
   * @param items
   * @return list of captures
   */
  public static List<Capture> fromListMapToCaptureList(List<Map<String, AttributeValue>> items) {
    return items.stream().map(CaptureMapper::fromMapToCapture).collect(Collectors.toList());
  }

  /**
   * Transform a capture to a Map<String, AttributeValue>
   * @param capture
   * @return
   */
  public static Map<String, AttributeValue> fromCaptureToMap(Capture capture) {
    return Map.of(
        "id", AttributeValue.builder().n(String.valueOf(capture.getId())).build(),
        "accountId", AttributeValue.builder().s(capture.getAccountId()).build(),
        "data", AttributeValue.builder().s(capture.getData()).build()
    );
  }
}
