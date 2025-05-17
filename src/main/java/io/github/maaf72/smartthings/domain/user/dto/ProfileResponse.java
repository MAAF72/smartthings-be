package io.github.maaf72.smartthings.domain.user.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProfileResponse {
  private UUID id;
  private String email;
  private String name;
  private LocalDate dateOfBirth;
  private String address;
  private String country;
  private String role;
  private List<Device> registeredDevices;
  private List<Device> createdDevices;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt; 

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
  public static class Device implements Serializable {
    private UUID id;
    private String brandName;
    private String deviceName;
    private String deviceDescription;
    private Integer value;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime registeredAt;
  }
}
