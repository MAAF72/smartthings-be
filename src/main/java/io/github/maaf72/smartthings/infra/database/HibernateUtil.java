package io.github.maaf72.smartthings.infra.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.model.naming.PhysicalNamingStrategySnakeCaseImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;
import org.hibernate.reactive.provider.ReactivePersistenceProvider;
import org.hibernate.reactive.provider.ReactiveServiceRegistryBuilder;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexReader;

import io.github.maaf72.smartthings.config.Config;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import jakarta.persistence.Entity;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class HibernateUtil {
  private SessionFactory sessionFactory;

  @Inject
  private BeanManager beanManager;

  @PostConstruct
  public void init() {
    Map<String, Object> settings = Map.ofEntries(
      Map.entry(AvailableSettings.JAKARTA_JDBC_URL, Config.APP_DATABASE_JDBC_URL),
      Map.entry(AvailableSettings.JAKARTA_JDBC_USER, Config.APP_DATABASE_USERNAME),
      Map.entry(AvailableSettings.JAKARTA_JDBC_PASSWORD, Config.APP_DATABASE_PASSWORD),
      Map.entry(AvailableSettings.IMPLICIT_NAMING_STRATEGY, ImplicitNamingStrategyJpaCompliantImpl.class.getName()),
      Map.entry(AvailableSettings.PHYSICAL_NAMING_STRATEGY, PhysicalNamingStrategySnakeCaseImpl.class.getName()),
      Map.entry("jakarta.persistence.provider", ReactivePersistenceProvider.class.getName()),
      Map.entry(AvailableSettings.JAKARTA_CDI_BEAN_MANAGER, beanManager),
      Map.entry(AvailableSettings.SHOW_SQL, true),
      Map.entry(AvailableSettings.FORMAT_SQL, true),
      Map.entry(AvailableSettings.HIGHLIGHT_SQL, true)
    );

  StandardServiceRegistry registry = new ReactiveServiceRegistryBuilder()
    .applySettings(settings)
    .build();

  MetadataSources metadataSources = new MetadataSources(registry);

  // Load Jandex index
  Index index;
  try {
    InputStream is = Thread
      .currentThread()
      .getContextClassLoader()
      .getResourceAsStream("META-INF/jandex.idx");
      
    index = new IndexReader(is).read();
  } catch (IOException e) {
    throw new RuntimeException("Failed to read Jandex index", e);
  }

  index.getKnownClasses().stream()
    .filter(classInfo -> classInfo.hasDeclaredAnnotation(Entity.class))
    .forEach(classInfo -> {
      String className = classInfo.name().toString();
      metadataSources.addAnnotatedClassName(className);
    });

    sessionFactory = metadataSources.buildMetadata().buildSessionFactory().unwrap(SessionFactory.class);
  }

  @PreDestroy
  public void cleanup() {
      if (sessionFactory != null) {
          sessionFactory.close();
      }
  }

  @Produces
  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void ensureInitialized() throws Exception {
    if (sessionFactory == null) {
      throw new RuntimeException("Database not initialized");
    }
  }
}
