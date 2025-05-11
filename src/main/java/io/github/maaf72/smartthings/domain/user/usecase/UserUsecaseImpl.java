package io.github.maaf72.smartthings.domain.user.usecase;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.github.maaf72.smartthings.domain.device.repository.DeviceRepository;
import io.github.maaf72.smartthings.domain.user.dto.Filter;
import io.github.maaf72.smartthings.domain.user.entity.User;
import io.github.maaf72.smartthings.domain.user.repository.UserRepository;
import io.github.maaf72.smartthings.itf.AppUsecaseItf;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserUsecaseImpl implements AppUsecaseItf, UserUsecase {

  @Inject
  UserRepository userRepository;
  
  @Inject
  DeviceRepository deviceRepository;

  @Override
  public Optional<User> findByID(UUID id) {
    return userRepository.findByID(id);
  }

  @Override
  public List<User> find(Filter filter) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'find'");
  }

  @Override
  public void update(User user) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'update'");
  }

  @Override
  public void delete(User user) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'delete'");
  }
  
}
