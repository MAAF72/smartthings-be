package io.github.maaf72.smartthings.domain.device.dto;

import java.io.Serializable;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateVendorDeviceRequest {
  @NotBlank
  private String brandName;
  @NotBlank
  private String deviceName;
  @NotBlank
  private String deviceDescription;
  @NotNull
  private DeviceConfiguration deviceConfiguration;
  @NotNull
  private Integer value;

  @Data
  @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
  public static class DeviceConfiguration implements Serializable {
    private Integer minValue;
    private Integer maxValue;
    private Integer defaultValue;
  }
}
