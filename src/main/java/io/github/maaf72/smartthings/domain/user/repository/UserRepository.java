package io.github.maaf72.smartthings.domain.user.repository;

import java.util.List;
import java.util.UUID;

import io.github.maaf72.smartthings.domain.common.dto.PaginationRequest;
import io.github.maaf72.smartthings.domain.user.entity.User;
import io.github.maaf72.smartthings.domain.user.entity.UserWithTotalRegisteredDevices;
import io.github.maaf72.smartthings.itf.AppRepositoryItf;
import io.smallrye.mutiny.Uni;

public interface UserRepository extends AppRepositoryItf<User, UUID> {
  Uni<User> findByEmail(String email);

  Uni<List<UserWithTotalRegisteredDevices>> findAllUserWithTotalRegisteredDevices(PaginationRequest page);

  Uni<Long> countAllUser();
  
  Uni<List<UserWithTotalRegisteredDevices>> findAllVendorWithTotalRegisteredDevices(PaginationRequest page);

  Uni<Long> countAllVendor();
}