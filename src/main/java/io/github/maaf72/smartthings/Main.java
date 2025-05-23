package io.github.maaf72.smartthings;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.maaf72.smartthings.config.Config;
import io.github.maaf72.smartthings.domain.device.handler.DeviceRoutes;
import io.github.maaf72.smartthings.domain.user.handler.UserRoutes;
import io.github.maaf72.smartthings.infra.database.HibernateUtil;
import io.github.maaf72.smartthings.infra.exception.ExceptionHandler;
import io.github.maaf72.smartthings.infra.mapper.CustomObjectMapper;
import io.github.maaf72.smartthings.infra.middleware.CorsMiddleware;
import io.github.maaf72.smartthings.infra.middleware.JwtAuthMiddleware;
import io.github.maaf72.smartthings.infra.middleware.TracerMiddleware;
import io.github.maaf72.smartthings.infra.oas.OasRoutes;
import io.github.maaf72.smartthings.itf.AppMiddlewareItf;
import io.github.maaf72.smartthings.itf.AppRoutesItf;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import ratpack.core.error.ServerErrorHandler;
import ratpack.core.server.RatpackServer;

@ApplicationScoped
@Slf4j
public class Main {
  private Weld weld;
  private WeldContainer container;

  public static void main(String[] args) throws Exception {
    new Main().start();
  }
  
  public void start() throws Exception {
    setupDependencies();
    setupWebServer();
  }
  
  private void setupDependencies() throws Exception {
    weld = new Weld();
    weld.addPackages(true, Main.class);
    container = weld.initialize();

    // Eager initialization
    container.select(HibernateUtil.class).get().ensureInitialized();
  }

  private void setupWebServer() throws Exception {
    List<AppMiddlewareItf> middlewareList = Arrays.asList(
      container.select(TracerMiddleware.class).get(),
      container.select(CorsMiddleware.class).get(),
      container.select(JwtAuthMiddleware.class).get()
    );

    List<AppRoutesItf> routesList = Arrays.asList(
      container.select(UserRoutes.class).get(),
      container.select(DeviceRoutes.class).get()
    );

    RatpackServer
      .start(server -> {
        server
          .serverConfig(cfg -> cfg
            .baseDir(new File("statics").getAbsoluteFile())
            .development(Config.APP_DEVELOPMENT)
            .port(Config.APP_PORT)
          )
          .registryOf(r -> r
            .add(ObjectMapper.class, CustomObjectMapper.getObjectMapper())
            .add(ServerErrorHandler.class, new ExceptionHandler())
          )
          .handlers(c -> {
            container.select(OasRoutes.class).get().Routes(c);
            middlewareList.forEach(c::all);
            c.prefix(Config.APP_API_PREFIX, cApi -> {
              routesList.forEach(r -> r.Routes(cApi));
            });
          });
      });
  }
}
