package io.github.maaf72.smartthings.domain.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.sql.DataSource;

import io.github.maaf72.smartthings.domain.user.dto.Filter;
import io.github.maaf72.smartthings.domain.user.entity.User;
import io.github.maaf72.smartthings.itf.AppRepositoryItf;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserRepositoryImpl implements AppRepositoryItf, UserRepository {

  @Inject
  DataSource db;

  @Override
  public Optional<User> findByID(UUID id) {
    return null;
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
