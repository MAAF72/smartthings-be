package io.github.maaf72.smartthings.infra.database;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import io.github.maaf72.smartthings.domain.common.dto.PaginationRequest;
import io.github.maaf72.smartthings.itf.AppRepositoryItf;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public abstract class BaseRepository<T, ID extends Serializable> implements AppRepositoryItf<T, ID> {
  private final Class<T> entityClass;

  public Optional<T> findById(ID id) {
    return Optional.ofNullable(doInSession(session -> session.get(entityClass, id)));
  }

  public Optional<T> findOne(Map<String, Object> filters) {
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

  public Optional<T> findOne(BiFunction<CriteriaBuilder, Root<T>, Predicate> filterFunction) {
    return doInSession(session -> {
      CriteriaBuilder cb = session.getCriteriaBuilder();
      CriteriaQuery<T> cq = cb.createQuery(entityClass);
      Root<T> root = cq.from(entityClass);
      cq.select(root);

      if (filterFunction != null) {
        cq.where(filterFunction.apply(cb, root));
      }

      return session.createQuery(cq).uniqueResultOptional();
    });
  }

  public List<T> findAll() {
    return findAll(null, null);
  }
  
  public List<T> findAll(BiFunction<CriteriaBuilder, Root<T>, Predicate> filterFunction) {
    return findAll(filterFunction, null);
  }

  public List<T> findAll(PaginationRequest page) {
    return findAll(null, page);
  }
  
  public List<T> findAll(BiFunction<CriteriaBuilder, Root<T>, Predicate> filterFunction, PaginationRequest page) {
    return doInSession(session -> {
      CriteriaBuilder cb = session.getCriteriaBuilder();
      CriteriaQuery<T> cq = cb.createQuery(entityClass);
      Root<T> root = cq.from(entityClass);
      cq.select(root);

      if (filterFunction != null) {
        cq.where(filterFunction.apply(cb, root));
      }
      
      Query<T> query = session.createQuery(cq);

      if (page != null) {
        query.setFirstResult(page.offset());
        query.setMaxResults(page.size);
      }

      return query.getResultList();
    });
  }
  
  public long countAll() {
    return countAll(null);
  }

  public long countAll(BiFunction<CriteriaBuilder, Root<T>, Predicate> filterFunction) {
    return doInSession(session -> {
      CriteriaBuilder cb = session.getCriteriaBuilder();
      CriteriaQuery<Long> cq = cb.createQuery(Long.class);
      Root<T> root = cq.from(entityClass);
      cq.select(cb.count(root));

      if (filterFunction != null) {
        cq.where(filterFunction.apply(cb, root));
      }

      return session.createQuery(cq).getSingleResult();
    });
  }

  public T create(T entity) {
    runInTransaction(session -> session.persist(entity));
    
    return entity;
  }

  public T update(T entity) {
    return doInTransaction(session -> session.merge(entity));
  }

  public void deleteById(ID id) {
    runInTransaction(session -> {
      T entity = session.get(entityClass, id);
      if (entity != null) {
        session.remove(entity);
      }
    });
  }

  public T getReference(ID id) {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      return session.getReference(entityClass, id);
    } catch (Exception e) {
      throw new RuntimeException("Reference operation failed", e);
    }
  }
  
  public static <R> R doInTransaction(Function<Session, R> function) {
    Transaction tx = null;
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      tx = session.beginTransaction();
      R result = function.apply(session);
      tx.commit();

      return result;
    } catch (Exception e) {
      if (tx != null && tx.getStatus().canRollback()) {
        tx.rollback();
      }

      throw new RuntimeException("Transaction failed", e);
    }
  }
  
  public static void runInTransaction(Consumer<Session> consumer) {
    doInTransaction(session -> {
      consumer.accept(session);

      return null;
    });
  }
  
  public static <R> R doInSession(Function<Session, R> function) {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      return function.apply(session);
    } catch (Exception e) {
      log.error("Session operation failed", e);
      throw new RuntimeException("Session operation failed", e);
    }
  }

  public static void runInSession(Consumer<Session> consumer) {
    doInSession(session -> {
      consumer.accept(session);

      return null;
    });
  }
}
