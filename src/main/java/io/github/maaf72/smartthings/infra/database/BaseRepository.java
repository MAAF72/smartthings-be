package io.github.maaf72.smartthings.infra.database;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.hibernate.reactive.mutiny.Mutiny.SelectionQuery;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

import io.github.maaf72.smartthings.domain.common.dto.PaginationRequest;
import io.github.maaf72.smartthings.itf.AppRepositoryItf;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseRepository<T, ID extends Serializable> implements AppRepositoryItf<T, ID> {
  private final Class<T> entityClass;
  protected final SessionFactory sessionFactory;

  public Uni<T> findById(ID id) {
    return sessionFactory.withSession(
      session -> session.find(entityClass, id)
    );
  }

  public Uni<T> findOne(Map<String, Object> filters) {
    return findOne((cb, root) -> 
      cb.and(filters.entrySet().stream()
        .map(entry -> {
            String[] keyParts = entry.getKey().split("\\.");
            Path<?> path = root;
            for (String part : keyParts) {
                path = path.get(part);
            }

          return cb.equal(path, entry.getValue());
        })
        .toArray(Predicate[]::new)
      )
    );
  }

  public Uni<T> findOne(BiFunction<CriteriaBuilder, Root<T>, Predicate> filterFunction) {
    return sessionFactory.withSession(
      session -> {
        CriteriaBuilder cb = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        cq.select(root);

        if (filterFunction != null) {
        cq.where(filterFunction.apply(cb, root));
      }

      return session.createQuery(cq).getSingleResultOrNull();
      }
    );
  }

  public Uni<List<T>> findAll() {
    return findAll(null, null);
  }
  
  public Uni<List<T>> findAll(BiFunction<CriteriaBuilder, Root<T>, Predicate> filterFunction) {
    return findAll(filterFunction, null);
  }

  public Uni<List<T>> findAll(PaginationRequest page) {
    return findAll(null, page);
  }
  
  public Uni<List<T>> findAll(BiFunction<CriteriaBuilder, Root<T>, Predicate> filterFunction, PaginationRequest page) {
    return sessionFactory.withSession(
      session -> {
        CriteriaBuilder cb = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        cq.select(root);

        if (filterFunction != null) {
          cq.where(filterFunction.apply(cb, root));
        }
        
        
        SelectionQuery<T> query = session.createQuery(cq);
        if (page != null) {
          query.setFirstResult(page.offset());
          query.setMaxResults(page.size);
        }

        return query.getResultList();
      }
    );
  }
  
  public Uni<Long> countAll() {
    return countAll(null);
  }

  public Uni<Long> countAll(BiFunction<CriteriaBuilder, Root<T>, Predicate> filterFunction) {
    return sessionFactory.withSession(
      session -> {
        CriteriaBuilder cb = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<T> root = cq.from(entityClass);
        cq.select(cb.count(root));

        if (filterFunction != null) {
          cq.where(filterFunction.apply(cb, root));
        }

        return session.createQuery(cq).getSingleResult();
      }
    );
  }

  public Uni<T> create(T entity) {
    return sessionFactory.withTransaction(
      session -> session.persist(entity).replaceWith(entity)
    );
  }

  public Uni<T> update(T entity) {
    return sessionFactory.withTransaction(
      session-> session.merge(entity)
    );
  }

  public Uni<Void> deleteById(ID id) {
    return sessionFactory.withTransaction(
      session -> session
        .find(entityClass, id)
        .onItem()
          .ifNotNull()
            .transformToUni(session::remove)
            .replaceWithVoid()
    );
  }

  public Uni<T> getReference(ID id) {
    return sessionFactory.withSession(
        session -> Uni.createFrom().item(session.getReference(entityClass, id))
    );
  }
}
