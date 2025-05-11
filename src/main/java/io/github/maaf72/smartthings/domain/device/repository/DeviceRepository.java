package io.github.maaf72.smartthings.domain.device.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.github.maaf72.smartthings.domain.device.dto.Filter;
import io.github.maaf72.smartthings.domain.device.entity.Device;

public interface DeviceRepository {
  Optional<Device> findByID(UUID id);

  List<Device> find(Filter filter);

  void update(Device device);

  void delete(Device device);
}
