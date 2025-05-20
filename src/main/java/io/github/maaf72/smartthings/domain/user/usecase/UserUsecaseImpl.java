package io.github.maaf72.smartthings.domain.user.usecase;

import java.util.List;
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
import io.smallrye.mutiny.Uni;
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

  public Uni<User> getUser(UUID actorId, Role role, UUID userId) {
    boolean isAllowed = 
      (false)
      || (actorId.equals(userId))
      || (role.equals(Role.ST_ADMINISTRATOR));
      
    if (!isAllowed) {
      return Uni.createFrom().failure(new HttpException(403, "you are not allowed to view this user"));
    }

    Uni<User> user = repository.findById(userId)
      .onItem()
      .ifNull()
        .failWith(() -> new HttpException(404, "user not found"));
    
    return user;
  }

  public Uni<List<UserWithTotalRegisteredDevices>> listUserWithTotalRegisteredDevices(UUID actorId, Role role, PaginationRequest page) {
    boolean isAllowed = (false)
      || (role.equals(Role.ST_ADMINISTRATOR));

    if (!isAllowed) {
      return Uni.createFrom().failure(new HttpException(403, "you are not allowed to view users data"));
    }

    return repository.findAllUserWithTotalRegisteredDevices(page);
  }
  
  public Uni<Long> countUsers() {
    return repository.countAllUser();
  }
 
  public Uni<List<UserWithTotalRegisteredDevices>> listVendorWithTotalRegisteredDevices(UUID actorId, Role role, PaginationRequest page) {
    boolean isAllowed = (false)
      || (role.equals(Role.ST_ADMINISTRATOR));

    if (!isAllowed) {
      return Uni.createFrom().failure(new HttpException(403, "you are not allowed to view vendors data"));
    }

    return repository.findAllVendorWithTotalRegisteredDevices(page);
  }
  
  public Uni<Long> countVendors() {
    return repository.countAllVendor();
  }
 
  public Uni<User> register(RegisterRequest request) {
    return repository.findByEmail(request.getEmail())
      .onItem()
      .ifNotNull()
        .failWith(() -> new HttpException(400, "email has been taken"))
      .flatMap(existingUser -> {
        String hash = HashUtil.hash(request.getPassword());
            
        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setHash(hash);
        user.setDateOfBirth(request.getDateOfBirth());
        user.setAddress(request.getAddress());
        user.setCountry(request.getCountry());
        user.setRole(Role.ST_USERS);

        return repository.create(user);
      });
  }
  
  public Uni<String> login(LoginRequest request) {
    return repository.findByEmail(request.getEmail())
      .onItem()
      .ifNull()
        .failWith(() -> new HttpException(401, "invalid credentials"))
      .chain(user -> {
        if (!HashUtil.verify(request.getPassword(), user.getHash())) {
          return Uni.createFrom().failure(() -> new HttpException(401, "invalid credentials"));
        }
        
        UserClaims claimsObj = new UserClaims();
        claimsObj.setId(user.getId());
        claimsObj.setEmail(user.getEmail());
        claimsObj.setName(user.getName());
        claimsObj.setRole(user.getRole());
        
        String token = JwtUtil.generateJWTToken(claimsObj);
        return Uni.createFrom().item(token);
      });
  }
}
    
