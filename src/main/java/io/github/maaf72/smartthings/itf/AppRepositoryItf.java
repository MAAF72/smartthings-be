package io.github.maaf72.smartthings.itf;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import io.github.maaf72.smartthings.domain.common.dto.PaginationRequest;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public interface AppRepositoryItf<T, ID extends Serializable> {
  Optional<T> findById(ID id);

  Optional<T> findOne(BiFunction<CriteriaBuilder, Root<T>, Predicate> filterFunction);

  List<T> findAll();

  List<T> findAll(PaginationRequest page);

  List<T> findAll(BiFunction<CriteriaBuilder, Root<T>, Predicate> filterFunction);
  
  List<T> findAll(BiFunction<CriteriaBuilder, Root<T>, Predicate> filterFunction, PaginationRequest page);

  long countAll();

  long countAll(BiFunction<CriteriaBuilder, Root<T>, Predicate> filterFunction);

  T create(T entity);

  T update(T entity);

  void deleteById(ID id);

  T getReference(ID id);
}
