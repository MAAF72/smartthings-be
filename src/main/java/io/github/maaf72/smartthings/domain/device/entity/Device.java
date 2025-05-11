package io.github.maaf72.smartthings.domain.device.entity;

import java.util.UUID;

import lombok.Data;

@Data
public class Device {
  private UUID id;
  private String name;
}
