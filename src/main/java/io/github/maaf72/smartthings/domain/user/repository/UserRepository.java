package io.github.maaf72.smartthings.domain.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.github.maaf72.smartthings.domain.common.dto.PaginationRequest;
import io.github.maaf72.smartthings.domain.user.entity.User;
import io.github.maaf72.smartthings.domain.user.entity.UserWithTotalRegisteredDevices;
import io.github.maaf72.smartthings.itf.AppRepositoryItf;

public interface UserRepository extends AppRepositoryItf<User, UUID> {
  Optional<User> findByEmail(String email);

  List<UserWithTotalRegisteredDevices> findAllUserWithTotalRegisteredDevices(PaginationRequest page);

  long countAllUser();
  
  List<UserWithTotalRegisteredDevices> findAllVendorWithTotalRegisteredDevices(PaginationRequest page);

  long countAllVendor();
}