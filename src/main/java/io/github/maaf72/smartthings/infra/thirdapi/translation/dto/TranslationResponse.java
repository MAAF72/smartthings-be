package io.github.maaf72.smartthings.infra.thirdapi.translation.dto;

import lombok.Data;

@Data
public class TranslationResponse {
  private final String translation;
  private final String country;
}
