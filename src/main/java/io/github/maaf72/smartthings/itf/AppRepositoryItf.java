package io.github.maaf72.smartthings.itf;

import java.io.Serializable;
import java.util.List;
import java.util.function.BiFunction;

import io.github.maaf72.smartthings.domain.common.dto.PaginationRequest;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public interface AppRepositoryItf<T, ID extends Serializable> {
  Uni<T> findById(ID id);

  Uni<T> findOne(BiFunction<CriteriaBuilder, Root<T>, Predicate> filterFunction);

  Uni<List<T>> findAll();

  Uni<List<T>> findAll(PaginationRequest page);

  Uni<List<T>> findAll(BiFunction<CriteriaBuilder, Root<T>, Predicate> filterFunction);
  
  Uni<List<T>> findAll(BiFunction<CriteriaBuilder, Root<T>, Predicate> filterFunction, PaginationRequest page);

  Uni<Long> countAll();

  Uni<Long> countAll(BiFunction<CriteriaBuilder, Root<T>, Predicate> filterFunction);

  Uni<T> create(T entity);

  Uni<T> update(T entity);

  Uni<Void> deleteById(ID id);

  Uni<T> getReference(ID id);
}
