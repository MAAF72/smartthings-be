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
import io.smallrye.mutiny.Uni;

public interface UserUsecase extends AppUsecaseItf {
  Uni<List<UserWithTotalRegisteredDevices>> listUserWithTotalRegisteredDevices(UUID actorId, Role role, PaginationRequest page);
  
  Uni<Long> countUsers();

  Uni<List<UserWithTotalRegisteredDevices>> listVendorWithTotalRegisteredDevices(UUID actorId, Role role, PaginationRequest page);
  
  Uni<Long> countVendors();

  Uni<User> getUser(UUID actorId, Role role, UUID userId);

  Uni<User> register(RegisterRequest request);

  Uni<String> login(LoginRequest request);
}
