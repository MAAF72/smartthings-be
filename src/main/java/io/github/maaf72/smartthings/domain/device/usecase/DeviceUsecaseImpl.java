package io.github.maaf72.smartthings.domain.device.usecase;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.type.TypeReference;

import io.github.maaf72.smartthings.domain.auditlog.entity.AuditLog;
import io.github.maaf72.smartthings.domain.auditlog.repository.AuditLogRepository;
import io.github.maaf72.smartthings.domain.common.dto.PaginationRequest;
import io.github.maaf72.smartthings.domain.device.dto.CreateDeviceRequest;
import io.github.maaf72.smartthings.domain.device.dto.UpdateDeviceRequest;
import io.github.maaf72.smartthings.domain.device.entity.Device;
import io.github.maaf72.smartthings.domain.device.entity.Device.DeviceConfiguration;
import io.github.maaf72.smartthings.domain.device.repository.DeviceRepository;
import io.github.maaf72.smartthings.domain.user.entity.User.Role;
import io.github.maaf72.smartthings.domain.user.repository.UserRepository;
import io.github.maaf72.smartthings.infra.exception.HttpException;
import io.github.maaf72.smartthings.infra.mapper.CustomModelMapper;
import io.github.maaf72.smartthings.infra.mapper.CustomObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class DeviceUsecaseImpl implements DeviceUsecase {
  private final static String MODULE = "DEVICE";
  
  @Inject
  DeviceRepository repository;

  @Inject
  UserRepository userRepository;

  @Inject
  AuditLogRepository auditLogRepository;

  public Device createDevice(UUID actorId, Role role, CreateDeviceRequest request) {
    if (!role.equals(Role.DEVICE_VENDOR)) {
      throw new HttpException(403, "you are not allowed to create a device");
    }

    Device device = new Device();

    device.setBrandName(request.getBrandName());
    device.setDeviceName(request.getDeviceName());
    device.setDeviceDescription(request.getDeviceDescription());
    device.setDeviceConfiguration(
        CustomModelMapper.getModelMapper().map(request.getDeviceConfiguration(), DeviceConfiguration.class));
    device.setValue(request.getValue());
    device.setCreatedBy(userRepository.getReference(actorId));

    device = repository.create(device);

    // do in asyncronous way
    {
      AuditLog auditLog = new AuditLog();

      auditLog.setModule(MODULE);
      auditLog.setAction("CREATE");
      auditLog.setSubject(actorId.toString());
      auditLog.setObject(device.getId().toString());
      auditLog.setMetadata(CustomObjectMapper.getObjectMapper().convertValue(device, new TypeReference<>() {
      }));

      auditLogRepository.create(auditLog);
    }

    return device;
  }
  
  public List<Device> listDevice(UUID actorId, Role role, PaginationRequest page) {
    switch (role) {
      case ST_ADMINISTRATOR: return repository.findAll(page);
      case Role.DEVICE_VENDOR: return repository.findAllVendorDevice(actorId, page);
      case Role.ST_USERS: return repository.findAllUserDevice(actorId, page);
      default: throw new HttpException(500, "unknown role");
    }
  }
  
  public long countDevice(UUID actorId, Role role) {
    switch (role) {
      case ST_ADMINISTRATOR: return repository.countAll();
      case Role.DEVICE_VENDOR: return repository.countAllVendorDevice(actorId);
      case Role.ST_USERS: return repository.countAllUserDevice(actorId);
      default: throw new HttpException(500, "unknown role");
    }
  }

  public List<Device> listAvailableDevice(PaginationRequest page) {
    return repository.findAllAvailableDevice(page);
  }

  public long countAvailableDevice() {
    return repository.countAllAvailableDevice();
  }

  public Device getDevice(UUID actorId, Role role, UUID deviceId) {
    Device device = repository.findById(deviceId).orElseThrow(() -> new HttpException(404, "device not found"));

    boolean isAllowed = 
      (false)
      || (role.equals(Role.DEVICE_VENDOR) && device.getCreatedBy() != null && device.getCreatedBy().getId().equals(actorId))
      || (role.equals(Role.ST_USERS) && (device.getRegisteredBy() == null || device.getRegisteredBy().getId().equals(actorId)));
      
    if (!isAllowed) {
      throw new HttpException(403, "you are not allowed to view this device");
    }

    return device; 
  }

  public Device updateDevice(UUID actorId, Role role, UpdateDeviceRequest request, UUID deviceId) {
    Device device = repository.findById(deviceId).orElseThrow(() -> new HttpException(404, "device not found"));

    boolean isAllowed = 
      (false)
      || (role.equals(Role.DEVICE_VENDOR) && device.getCreatedBy() != null && device.getCreatedBy().getId().equals(actorId))
      || (role.equals(Role.ST_USERS) && device.getRegisteredBy() != null && device.getRegisteredBy().getId().equals(actorId));

    if (!isAllowed) {
      throw new HttpException(403, "you are not allowed to update this device");
    }

    // Users can only update their own devices value 
    if (role.equals(Role.ST_USERS)) {
      if (device.getDeviceConfiguration() != null) {
        DeviceConfiguration cfg = device.getDeviceConfiguration();
        if (cfg.getMinValue() > request.getValue() || cfg.getMaxValue() < request.getValue()) {
          throw new HttpException(400, "device value must be between " + cfg.getMinValue() + " and " + cfg.getMaxValue());
        }
      }
      
      device.setValue(request.getValue());
    } else {
      device.setBrandName(request.getBrandName());
      device.setDeviceName(request.getDeviceName());
      device.setDeviceDescription(request.getDeviceDescription());
      device.setDeviceConfiguration(
        CustomModelMapper.getModelMapper().map(request.getDeviceConfiguration(), DeviceConfiguration.class)
      );
      device.setValue(request.getValue());
    }
  
    device = repository.update(device);

    // do in asyncronous way
    {
      AuditLog auditLog = new AuditLog();

      auditLog.setModule(MODULE);
      auditLog.setAction("UPDATE");
      auditLog.setSubject(actorId.toString());
      auditLog.setObject(device.getId().toString());
      auditLog.setMetadata(CustomObjectMapper.getObjectMapper().convertValue(device, new TypeReference<>() {
      }));

      auditLogRepository.create(auditLog);
    }
    
    return device;
  }

  public void deleteDevice(UUID actorId, Role role, UUID deviceId) {
    Device device = repository.findById(deviceId).orElseThrow(() -> new HttpException(404, "device not found"));

    boolean isAllowed = 
      (false)
      || (role.equals(Role.DEVICE_VENDOR) && device.getCreatedBy() != null && device.getCreatedBy().getId().equals(actorId));

    if (!isAllowed) {
      throw new HttpException(403, "you are not allowed to delete this device");
    }

    if (device.getRegisteredBy() != null) {
      throw new HttpException(403, "you can not delete registered device");
    }

    repository.deleteById(deviceId);

    // do in asyncronous way
    {
      AuditLog auditLog = new AuditLog();

      auditLog.setModule(MODULE);
      auditLog.setAction("DELETE");
      auditLog.setSubject(actorId.toString());
      auditLog.setObject(device.getId().toString());
      auditLog.setMetadata(CustomObjectMapper.getObjectMapper().convertValue(device, new TypeReference<>() {
      }));

      auditLogRepository.create(auditLog);
    }
  }
  
  public void registerDevice(UUID actorId, Role role, UUID deviceId) {
    Device device = repository.findById(deviceId).orElseThrow(() -> new HttpException(404, "device not found"));

    if (!role.equals(Role.ST_USERS)) {
      throw new HttpException(403, "you can not register this device");
    }

    if (device.getRegisteredBy() != null) {
      throw new HttpException(400, "this device is already registered");
    }

    device.setRegisteredBy(userRepository.getReference(actorId));

    device = repository.update(device);

    // do in asyncronous way
    {
      AuditLog auditLog = new AuditLog();

      auditLog.setModule(MODULE);
      auditLog.setAction("REGISTER");
      auditLog.setSubject(actorId.toString());
      auditLog.setObject(device.getId().toString());
      auditLog.setMetadata(CustomObjectMapper.getObjectMapper().convertValue(device, new TypeReference<>() {
      }));

      auditLogRepository.create(auditLog);
    }

    return;
  }
  
  public void unregisterDevice(UUID actorId, Role role, UUID deviceId) {
    Device device = repository.findById(deviceId).orElseThrow(() -> new HttpException(404, "device not found"));

    if (device.getRegisteredBy() == null) {
      throw new HttpException(400, "this device is not registered");
    }

    boolean isAllowed = 
      (false)
      || (role.equals(Role.ST_USERS) && device.getRegisteredBy() != null && device.getRegisteredBy().getId().equals(actorId));

    if (!isAllowed) {
      throw new HttpException(403, "you can not unregister this device");
    }

    device.setRegisteredBy(null);
  
    device = repository.update(device);

    // do in asyncronous way
    {
      AuditLog auditLog = new AuditLog();

      auditLog.setModule(MODULE);
      auditLog.setAction("UNREGISTER");
      auditLog.setSubject(actorId.toString());
      auditLog.setObject(device.getId().toString());
      auditLog.setMetadata(CustomObjectMapper.getObjectMapper().convertValue(device, new TypeReference<>() {}));

      auditLogRepository.create(auditLog);
    }
    
    return;
  }
}
