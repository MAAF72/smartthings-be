package io.github.maaf72.smartthings.domain.device.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class BasicUserResponse {
  private UUID id;
  private String email;
  private String name;
  private LocalDate dateOfBirth;
  private String address;
  private String country;
  private String role;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt; 
}
