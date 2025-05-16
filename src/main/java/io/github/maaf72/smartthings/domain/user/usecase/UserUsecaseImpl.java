package io.github.maaf72.smartthings.domain.user.usecase;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.github.maaf72.smartthings.domain.auditlog.repository.AuditLogRepository;
import io.github.maaf72.smartthings.domain.common.dto.PaginationRequest;
import io.github.maaf72.smartthings.domain.device.repository.DeviceRepository;
import io.github.maaf72.smartthings.domain.user.dto.LoginRequest;
import io.github.maaf72.smartthings.domain.user.dto.RegisterRequest;
import io.github.maaf72.smartthings.domain.user.entity.User;
import io.github.maaf72.smartthings.domain.user.entity.User.Role;
import io.github.maaf72.smartthings.domain.user.entity.UserWithTotalRegisteredDevices;
import io.github.maaf72.smartthings.domain.user.repository.UserRepository;
import io.github.maaf72.smartthings.infra.exception.HttpException;
import io.github.maaf72.smartthings.infra.security.HashUtil;
import io.github.maaf72.smartthings.infra.security.JwtUtil;
import io.github.maaf72.smartthings.infra.security.UserClaims;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserUsecaseImpl implements UserUsecase {

  @Inject
  UserRepository repository;

  @Inject
  DeviceRepository deviceRepository;

  @Inject
  AuditLogRepository auditLogRepository;

  public User getUser(UUID actorId, Role role, UUID userId) {
    boolean isAllowed = 
      (false)
      || (actorId.equals(userId))
      || (role.equals(Role.ST_ADMINISTRATOR));
      
    if (!isAllowed) {
      throw new HttpException(403, "you are not allowed to view this user");
    }

    User user = repository.findById(userId).orElseThrow(() -> new HttpException(404, "user not found"));
    
    // add preload devices
    return user;
  }

  public List<UserWithTotalRegisteredDevices> listUserWithTotalRegisteredDevices(UUID actorId, Role role, PaginationRequest page) {
    boolean isAllowed = (false)
      || (role.equals(Role.ST_ADMINISTRATOR));

    if (!isAllowed) {
      throw new HttpException(403, "you are not allowed to view users data");
    }

    return repository.findAllUserWithTotalRegisteredDevices(page);
  }
  
  public long countUsers() {
    return repository.countAllUser();
  }
 
  public List<UserWithTotalRegisteredDevices> listVendorWithTotalRegisteredDevices(UUID actorId, Role role, PaginationRequest page) {
    boolean isAllowed = (false)
      || (role.equals(Role.ST_ADMINISTRATOR));

    if (!isAllowed) {
      throw new HttpException(403, "you are not allowed to view vendors data");
    }

    return repository.findAllVendorWithTotalRegisteredDevices(page);
  }
  
  public long countVendors() {
    return repository.countAllVendor();
  }
 
  public User register(RegisterRequest request) {
    Optional<User> userOpt = repository.findByEmail(request.getEmail());

    if (userOpt.isPresent()) {
      throw new HttpException(400, "email has been taken");
    }

    String hash = HashUtil.hash(request.getPassword());

    User user = new User();
    user.setEmail(request.getEmail());
    user.setName(request.getName());
    user.setHash(hash);
    user.setDateOfBirth(request.getDateOfBirth());
    user.setAddress(request.getAddress());
    user.setCountry(request.getCountry());
    user.setRole(Role.ST_USERS);

    user = repository.create(user);

    return user;
  }
  
  public String login(LoginRequest request) {
    Optional<User> userOpt = repository.findByEmail(request.getEmail());

    User user = userOpt.orElseThrow(() -> new HttpException(401, "invalid credentials"));

    if (!HashUtil.verify(request.getPassword(), user.getHash())) {
      throw new HttpException(401, "invalid credentials");
    }

    UserClaims claimsObj = new UserClaims();
    
    claimsObj.setId(user.getId());
    claimsObj.setEmail(user.getEmail());
    claimsObj.setName(user.getName());
    claimsObj.setRole(user.getRole());

    String token = JwtUtil.generateJWTToken(user);

    return token;
  }

}
    
