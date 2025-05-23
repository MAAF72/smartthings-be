package io.github.maaf72.smartthings.domain.device.usecase;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;

import io.github.maaf72.smartthings.domain.auditlog.entity.AuditLog;
import io.github.maaf72.smartthings.domain.auditlog.repository.AuditLogRepository;
import io.github.maaf72.smartthings.domain.common.dto.PaginationRequest;
import io.github.maaf72.smartthings.domain.common.util.OperationUtil;
import io.github.maaf72.smartthings.domain.device.dto.CreateVendorDeviceRequest;
import io.github.maaf72.smartthings.domain.device.dto.UpdateVendorDeviceRequest;
import io.github.maaf72.smartthings.domain.device.entity.Device;
import io.github.maaf72.smartthings.domain.device.entity.Device.DeviceConfiguration;
import io.github.maaf72.smartthings.domain.device.repository.DeviceRepository;
import io.github.maaf72.smartthings.domain.user.entity.User.Role;
import io.github.maaf72.smartthings.domain.user.repository.UserRepository;
import io.github.maaf72.smartthings.infra.exception.HttpException;
import io.github.maaf72.smartthings.infra.mapper.CustomModelMapper;
import io.github.maaf72.smartthings.infra.mapper.CustomObjectMapper;
import io.github.maaf72.smartthings.infra.thirdapi.translation.TranslationService;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.smallrye.mutiny.Uni;
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

  @Inject
  TranslationService translationService;

  @WithSpan
  public Uni<Device> createDevice(UUID actorId, Role role, CreateVendorDeviceRequest request) {
    if (!role.equals(Role.DEVICE_VENDOR)) {
      return Uni.createFrom().failure(new HttpException(403, "you are not allowed to create a device"));
    }

    return userRepository.findById(actorId)
      .onItem()
      .ifNull()
        .failWith(() -> new HttpException(404, "missing user data"))
      .chain(user -> {
        Device device = new Device();

        device.setBrandName(request.getBrandName());
        device.setDeviceName(request.getDeviceName());
        device.setDeviceDescription(request.getDeviceDescription());
        device.setDeviceConfiguration(
            CustomModelMapper.getModelMapper().map(request.getDeviceConfiguration(), DeviceConfiguration.class));
        device.setValue(request.getValue());
        
        device.setCreatedBy(user);

        return repository.create(device)
          .onFailure()
            .transform(_ -> new HttpException(500, "failed to create device"))
          .chain(persistedDevice -> {
            AuditLog auditLog = new AuditLog();

            auditLog.setModule(MODULE);
            auditLog.setAction("CREATE");
            auditLog.setSubject(actorId.toString());
            auditLog.setObject(persistedDevice.getId().toString());
            auditLog.setMetadata(
              CustomObjectMapper.getObjectMapper().convertValue(persistedDevice, new TypeReference<>() {})
            );

            return auditLogRepository.create(auditLog).
              onFailure()
                .transform(_ -> new HttpException(500, "failed to log created device"))
              .replaceWith(persistedDevice);
          });
      });
  }
  
  @WithSpan
  public Uni<List<Device>> listDevice(UUID actorId, Role role, PaginationRequest page) {
    return userRepository.findById(actorId)
      .onItem()
      .ifNull()
        .failWith(() -> new HttpException(404, "user not found"))
      .chain(user -> {
        switch (role) {
          case ST_ADMINISTRATOR: 
            return repository.findAll(page);
          case DEVICE_VENDOR: 
            return repository.findAllVendorDevice(actorId, page);
          case ST_USERS:
            return repository.findAllUserDevice(actorId, page).chain(devices -> translateListDevice(devices, user.getCountry()));
          default:
            return Uni.createFrom().failure(new HttpException(403, "Unauthorized role: " + role));
        }
      });
  }
  
  @WithSpan
  public Uni<Long> countDevice(UUID actorId, Role role) {
    switch (role) {
      case ST_ADMINISTRATOR: return repository.countAll();
      case Role.DEVICE_VENDOR: return repository.countAllVendorDevice(actorId);
      case Role.ST_USERS: return repository.countAllUserDevice(actorId);
      default: return Uni.createFrom().failure(new HttpException(500, "unknown role"));
    }
  }

  @WithSpan
  public Uni<List<Device>> listAvailableDevice(UUID actorId, PaginationRequest page) {
    return userRepository.findById(actorId)
      .onItem()
      .ifNull()
        .failWith(() -> new HttpException(404, "user not found"))
      .chain(user -> repository.findAllAvailableDevice(page)
          .chain(listAvailableDevice -> translateListDevice(listAvailableDevice, user.getCountry()))
      );
  }

  @WithSpan
  public Uni<Long> countAvailableDevice() {
    return repository.countAllAvailableDevice();
  }

  @WithSpan
  public Uni<Device> getDevice(UUID actorId, Role role, UUID deviceId) {
    return repository.findById(deviceId)
      .onItem()
      .ifNull()
        .failWith(() -> new HttpException(404, "device not found"))
      .chain(device -> {
        boolean isAllowed = OperationUtil.or(
          (role.equals(Role.DEVICE_VENDOR) && device.getCreatedBy() != null && device.getCreatedBy().getId().equals(actorId)),
          (role.equals(Role.ST_USERS) && (device.getRegisteredBy() == null || device.getRegisteredBy().getId().equals(actorId)))
        );
          
        if (!isAllowed) {
          return Uni.createFrom().failure(new HttpException(403, "you are not allowed to view this device"));
        }

        return Uni.createFrom().item(device);
      });
  }

  @WithSpan
  public Uni<Device> updateDevice(UUID actorId, Role role, UpdateVendorDeviceRequest request, UUID deviceId) {
    return repository.findById(deviceId)
      .onItem()
      .ifNull()
        .failWith(() -> new HttpException(404, "device not found"))
      .chain(device -> {
        boolean isAllowed = OperationUtil.or(
          (role.equals(Role.DEVICE_VENDOR) && device.getCreatedBy() != null && device.getCreatedBy().getId().equals(actorId)),
          (role.equals(Role.ST_USERS) && device.getRegisteredBy() != null && device.getRegisteredBy().getId().equals(actorId))
        );

        if (!isAllowed) {
          return Uni.createFrom().failure(new HttpException(403, "you are not allowed to update this device"));
        }

        // Users can only update their own devices value 
        if (role.equals(Role.ST_USERS)) {
          if (device.getDeviceConfiguration() != null) {
            DeviceConfiguration cfg = device.getDeviceConfiguration();
            if (cfg.getMinValue() > request.getValue() || cfg.getMaxValue() < request.getValue()) {
              return Uni.createFrom().failure(new HttpException(400, "device value must be between " + cfg.getMinValue() + " and " + cfg.getMaxValue()));
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

        return repository.update(device)
          .onFailure()
            .transform(_ -> new HttpException(500, "failed to update device"))
          .chain(persistedDevice -> {
            AuditLog auditLog = new AuditLog();

            auditLog.setModule(MODULE);
            auditLog.setAction("UPDATE");
            auditLog.setSubject(actorId.toString());
            auditLog.setObject(persistedDevice.getId().toString());
            auditLog.setMetadata(
              CustomObjectMapper.getObjectMapper().convertValue(persistedDevice, new TypeReference<>() {})
            );

            return auditLogRepository.create(auditLog).
              onFailure()
                .transform(_ -> new HttpException(500, "failed to log updated device"))
              .replaceWith(persistedDevice);
          });
      });
  }

  @WithSpan
  public Uni<Void> deleteDevice(UUID actorId, Role role, UUID deviceId) {
    return repository.findById(deviceId)
      .onItem()
      .ifNull()
        .failWith(() -> new HttpException(404, "device not found"))
      .chain(device -> {
        boolean isAllowed = (role.equals(Role.DEVICE_VENDOR) && device.getCreatedBy() != null && device.getCreatedBy().getId().equals(actorId));

        if (!isAllowed) {
          return Uni.createFrom().failure(new HttpException(403, "you are not allowed to delete this device"));
        }

        if (device.getRegisteredBy() != null) {
          return Uni.createFrom().failure(new HttpException(403, "you can not delete registered device"));
        }

        return repository.deleteById(deviceId)
          .onFailure()
            .transform(_ -> new HttpException(500, "failed to delete device"))
          .chain(_ -> {
            AuditLog auditLog = new AuditLog();

            auditLog.setModule(MODULE);
            auditLog.setAction("DELETE");
            auditLog.setSubject(actorId.toString());
            auditLog.setObject(device.getId().toString());
            auditLog.setMetadata(
              CustomObjectMapper.getObjectMapper().convertValue(device, new TypeReference<>() {})
            );

            return auditLogRepository.create(auditLog).
              onFailure()
                .transform(_ -> new HttpException(500, "failed to log deleted device"))
              .replaceWithVoid();
          });
      });
  }
  
  @WithSpan
  public Uni<Void> registerDevice(UUID actorId, Role role, UUID deviceId) {
    return repository.findById(deviceId)
      .onItem()
      .ifNull()
        .failWith(() -> new HttpException(404, "device not found"))
      .chain(device -> {
        if (!role.equals(Role.ST_USERS)) {
          return Uni.createFrom().failure(new HttpException(403, "you can not register this device"));
        }

        if (device.getRegisteredBy() != null) {
          return Uni.createFrom().failure(new HttpException(400, "this device is already registered"));
        }

        return userRepository.getReference(actorId)
          .onItem()
          .ifNull()
            .failWith(() -> new HttpException(404, "missing user data"))
          .chain(user -> {
            device.setRegisteredAt(LocalDateTime.now());
            device.setRegisteredBy(user);

            return repository.update(device)
              .onFailure()
                .transform(_ -> new HttpException(500, "failed to register device"))
              .chain(persistedDevice -> {
                AuditLog auditLog = new AuditLog();

                auditLog.setModule(MODULE);
                auditLog.setAction("REGISTER");
                auditLog.setSubject(actorId.toString());
                auditLog.setObject(persistedDevice.getId().toString());
                auditLog.setMetadata(
                  CustomObjectMapper.getObjectMapper().convertValue(persistedDevice, new TypeReference<>() {})
                );

                return auditLogRepository.create(auditLog)
                  .onFailure()
                    .transform(_ -> new HttpException(500, "failed to log registered device"))
                  .replaceWithVoid();
              });
          });
      });
  }
  
  @WithSpan
  public Uni<Void> unregisterDevice(UUID actorId, Role role, UUID deviceId) {
    return repository.findById(deviceId)
      .onItem()
      .ifNull()
        .failWith(() -> new HttpException(404, "device not found"))
      .chain(device -> {
        if (device.getRegisteredBy() == null) {
          return Uni.createFrom().failure(new HttpException(400, "this device is not registered"));
        }

        boolean isAllowed = (role.equals(Role.ST_USERS) && device.getRegisteredBy() != null && device.getRegisteredBy().getId().equals(actorId));

        if (!isAllowed) {
          return Uni.createFrom().failure(new HttpException(403, "you can not unregister this device"));
        }

        device.setRegisteredBy(null);
        device.setRegisteredAt(null);

        return repository.update(device)
          .onFailure()
            .transform(_ -> new HttpException(500, "failed to unregister device"))
          .chain(_ -> {
            AuditLog auditLog = new AuditLog();

            auditLog.setModule(MODULE);
            auditLog.setAction("UNREGISTER");
            auditLog.setSubject(actorId.toString());
            auditLog.setObject(device.getId().toString());
            auditLog.setMetadata(
              CustomObjectMapper.getObjectMapper().convertValue(device, new TypeReference<>() {})
            );

            return auditLogRepository.create(auditLog)
              .onFailure()
                .transform(_ -> new HttpException(500, "failed to log unregistered device"))
              .replaceWithVoid();
          });
      });
  }
  
  @WithSpan
  private Uni<List<Device>> translateListDevice(List<Device> listDevice, String country) {
    if (listDevice == null || listDevice.isEmpty()) {
        return Uni.createFrom().item(Collections.emptyList());
    }

    return Uni.combine().all().unis(
      listDevice.stream()
        .map(device -> translateDevice(device, country))
        .collect(Collectors.toList())
    )
    .with(Device.class, Function.identity());
  }

  @WithSpan
  private Uni<Device> translateDevice(Device device, String country) {
    return translationService.SingleCountryTranslate(device.getDeviceDescription(), country)
      .map(translatedText -> {
        device.setDeviceDescription(translatedText);

        return device;
      });
  }
}
