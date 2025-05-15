package io.github.maaf72.smartthings.domain.user.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
  @NotBlank
  @Email
  private String email;
  @NotBlank
  private String password;
}