package io.nbugash.titan.core.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Capture {

  private Long id;
  private String data;
}
