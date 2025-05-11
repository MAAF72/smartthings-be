package io.github.maaf72.smartthings.domain.user.entity;

import java.util.UUID;

import lombok.Data;

@Data
public class User {
  private UUID id;
  private String name;
}
