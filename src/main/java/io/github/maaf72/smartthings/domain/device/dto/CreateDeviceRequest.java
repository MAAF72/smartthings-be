package io.github.maaf72.smartthings.domain.device.dto;

import java.io.Serializable;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateDeviceRequest {
  @NotBlank
  private String brandName;
  @NotBlank
  private String deviceName;
  @NotBlank
  private String deviceDescription;
  private DeviceConfiguration deviceConfiguration;
  private Integer value;

  @Data
  public static class DeviceConfiguration implements Serializable {
    private Integer minValue;
    private Integer maxValue;
    private Integer defaultValue;
  }
}
