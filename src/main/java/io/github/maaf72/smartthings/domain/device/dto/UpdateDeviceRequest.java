package io.github.maaf72.smartthings.domain.device.dto;

import java.io.Serializable;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.annotation.Nullable;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateDeviceRequest {
  @Nullable
  private String brandName;
  @Nullable
  private String deviceName;
  @Nullable
  private String deviceDescription;
  @Nullable
  private DeviceConfiguration deviceConfiguration;
  @Nullable
  private Integer value;

  @Data
  @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
  public static class DeviceConfiguration implements Serializable {
    private Integer minValue;
    private Integer maxValue;
    private Integer defaultValue;
  }
}
