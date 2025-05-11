package io.github.maaf72.smartthings.infra.database;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.github.maaf72.smartthings.config.Config;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class DataSourceFactory {
  private HikariDataSource dataSource;

  @Produces
  public DataSource getDataSource() {
    if (dataSource == null) {
      HikariConfig cfg = new HikariConfig();
      
      cfg.setDriverClassName(Config.APP_DATABASE_DRIVER);
      cfg.setJdbcUrl(Config.APP_DATABASE_JDBC_URL);
      cfg.setUsername(Config.APP_DATABASE_USERNAME);
      cfg.setPassword(Config.APP_DATABASE_PASSWORD);

      dataSource = new HikariDataSource(cfg);
    }

    return dataSource;
  }
}