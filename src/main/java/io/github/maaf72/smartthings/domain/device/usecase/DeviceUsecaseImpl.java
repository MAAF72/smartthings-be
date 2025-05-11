package io.github.maaf72.smartthings.domain.device.usecase;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.github.maaf72.smartthings.domain.device.dto.Filter;
import io.github.maaf72.smartthings.domain.device.entity.Device;
import io.github.maaf72.smartthings.domain.device.repository.DeviceRepository;
import io.github.maaf72.smartthings.itf.AppUsecaseItf;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DeviceUsecaseImpl implements AppUsecaseItf, DeviceUsecase {
  
  @Inject
  DeviceRepository repository;

  @Override
  public Optional<Device> findByID(UUID id) {
    return repository.findByID(id);
  }

  @Override
  public List<Device> find(Filter filter) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'find'");
  }

  @Override
  public void update(Device device) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'update'");
  }

  @Override
  public void delete(Device device) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'delete'");
  }
  
}
