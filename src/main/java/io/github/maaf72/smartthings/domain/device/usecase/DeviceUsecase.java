package io.github.maaf72.smartthings.domain.device.usecase;

import java.util.List;
import java.util.UUID;

import io.github.maaf72.smartthings.domain.common.dto.PaginationRequest;
import io.github.maaf72.smartthings.domain.device.dto.CreateDeviceRequest;
import io.github.maaf72.smartthings.domain.device.dto.UpdateDeviceRequest;
import io.github.maaf72.smartthings.domain.device.entity.Device;
import io.github.maaf72.smartthings.domain.user.entity.User.Role;
import io.github.maaf72.smartthings.itf.AppUsecaseItf;

public interface DeviceUsecase extends AppUsecaseItf {
  Device createDevice(UUID actorId, Role role, CreateDeviceRequest request);

  List<Device> listDevice(UUID actorId, Role role, PaginationRequest page);

  long countDevice(UUID actorId, Role role);
  
  List<Device> listAvailableDevice(UUID actorId, PaginationRequest page);
  
  long countAvailableDevice();

  Device getDevice(UUID actorId, Role role, UUID deviceId);

  Device updateDevice(UUID actorId, Role role, UpdateDeviceRequest request, UUID deviceId);

  void deleteDevice(UUID actorId, Role role, UUID deviceId);

  void registerDevice(UUID actorId, Role role, UUID deviceId);
  
  void unregisterDevice(UUID actorId, Role role, UUID deviceId);
  
}
