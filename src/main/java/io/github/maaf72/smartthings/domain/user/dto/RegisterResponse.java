package io.github.maaf72.smartthings.domain.user.dto;

import java.util.UUID;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.github.maaf72.smartthings.domain.user.entity.User.Role;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RegisterResponse {
  private final UUID userId;
  private final String email;
  private final Role role;
}
