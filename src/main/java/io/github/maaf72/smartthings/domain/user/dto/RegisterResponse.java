package io.github.maaf72.smartthings.domain.user.dto;

import java.util.UUID;

import io.github.maaf72.smartthings.domain.user.entity.User.Role;
import lombok.Data;

@Data
public class RegisterResponse {
  private final UUID userId;
  private final String email;
  private final Role role;
}
