package io.github.maaf72.smartthings.domain.user.usecase;

import java.util.List;
import java.util.UUID;

import io.github.maaf72.smartthings.domain.common.dto.PaginationRequest;
import io.github.maaf72.smartthings.domain.user.dto.LoginRequest;
import io.github.maaf72.smartthings.domain.user.dto.RegisterRequest;
import io.github.maaf72.smartthings.domain.user.entity.User;
import io.github.maaf72.smartthings.domain.user.entity.User.Role;
import io.github.maaf72.smartthings.domain.user.entity.UserWithTotalRegisteredDevices;
import io.github.maaf72.smartthings.itf.AppUsecaseItf;

public interface UserUsecase extends AppUsecaseItf {
  List<UserWithTotalRegisteredDevices> listUserWithTotalRegisteredDevices(UUID actorId, Role role, PaginationRequest page);
  
  long countUsers();

  List<UserWithTotalRegisteredDevices> listVendorWithTotalRegisteredDevices(UUID actorId, Role role, PaginationRequest page);
  
  long countVendors();

  User getUser(UUID actorId, Role role, UUID userId);

  User register(RegisterRequest request);

  String login(LoginRequest request);
}
