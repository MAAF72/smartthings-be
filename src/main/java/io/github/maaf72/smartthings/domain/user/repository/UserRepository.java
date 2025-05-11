package io.github.maaf72.smartthings.domain.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.github.maaf72.smartthings.domain.user.dto.Filter;
import io.github.maaf72.smartthings.domain.user.entity.User;

public interface UserRepository {
  Optional<User> findByID(UUID id);

  List<User> find(Filter filter);

  void update(User user);
  
  void delete(User user);
}
