package io.github.maaf72.smartthings.domain.user.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {
  @NotBlank
  @Email
  private String email;
  @NotBlank
  private String password;
  @NotBlank
  private String name;
  @NotNull
  private LocalDate dateOfBirth;
  @NotBlank
  private String address;
  @NotBlank
  private String country;
}