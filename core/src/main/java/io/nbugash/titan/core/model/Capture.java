package io.nbugash.titan.core.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Capture implements Serializable {
  private Long id;
  private String data;
  private String accountId;
}
