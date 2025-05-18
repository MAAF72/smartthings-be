package io.github.maaf72.smartthings.infra.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexReader;

import io.github.maaf72.smartthings.config.Config;
import jakarta.annotation.PostConstruct;
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
    Map<String, Object> settings = Map.of(
      AvailableSettings.JAKARTA_JDBC_URL, Config.APP_DATABASE_JDBC_URL,
      AvailableSettings.JAKARTA_JDBC_USER, Config.APP_DATABASE_USERNAME,
      AvailableSettings.JAKARTA_JDBC_PASSWORD, Config.APP_DATABASE_PASSWORD,
      AvailableSettings.GLOBALLY_QUOTED_IDENTIFIERS, true,
      AvailableSettings.GLOBALLY_QUOTED_IDENTIFIERS_SKIP_COLUMN_DEFINITIONS, true,
      AvailableSettings.PHYSICAL_NAMING_STRATEGY, CamelCaseToUnderscoresNamingStrategy.class.getName(),
      AvailableSettings.JAKARTA_CDI_BEAN_MANAGER, beanManager,
      AvailableSettings.SHOW_SQL, true,
      AvailableSettings.FORMAT_SQL, true,
      AvailableSettings.HIGHLIGHT_SQL, true
    );

  StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
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

    sessionFactory =  metadataSources.buildMetadata().buildSessionFactory();
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

  public String getTableName(final Class<?> cls) {
    SessionFactoryImplementor sfi = sessionFactory.unwrap(SessionFactoryImplementor.class);
    AbstractEntityPersister persister = (AbstractEntityPersister) sfi.getMappingMetamodel().getEntityDescriptor(cls);

    String dirtyName = persister.getIdentifierTableName();
    String cleanName = dirtyName.replace("\"", "");
    
    return cleanName;
  }
}
