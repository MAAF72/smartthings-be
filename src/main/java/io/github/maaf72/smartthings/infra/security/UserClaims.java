package io.github.maaf72.smartthings.infra.security;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.github.maaf72.smartthings.domain.user.entity.User;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserClaims {
  private UUID id;
  private String email;
  private String name;
  private User.Role role;
}
