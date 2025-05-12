package io.github.maaf72.smartthings.domain.user.handler;

import io.github.maaf72.smartthings.domain.user.entity.User.Role;
import io.github.maaf72.smartthings.infra.middleware.RbacMiddleware;
import io.github.maaf72.smartthings.itf.AppRoutesItf;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import ratpack.core.handling.Chain;

@ApplicationScoped
@Slf4j
public class UserRoutes implements AppRoutesItf {
  @Inject
  RegisterHandler registerHandler;

  @Inject
  LoginHandler loginHandler;

  @Inject
  GetProfileHandler getProfileHandler;

  @Inject
  ListUserHandler listUserHandler;

  @Inject
  GetUserHandler getUserHandler;

  @Inject
  ListVendorHandler listVendorHandler;

  @Inject
  GetVendorHandler getVendorHandler;
  

  @Override
  public void Routes(Chain chain) {
    try {
      chain
        .prefix("auth", x -> x
          .post("register", registerHandler)
          .post("login", loginHandler)
        )
        .prefix("users", x -> x
          .get("me", getProfileHandler)
        )
        .prefix("admin", x -> x
          .all(new RbacMiddleware(Role.ST_ADMINISTRATOR))
          .get("users", listUserHandler)
          .get("users/:id", getUserHandler)
          .get("vendors", listVendorHandler)
          .get("vendors/:id", getVendorHandler)
        );
    } catch (Exception e) {
      log.error("Error registering routes", e);
    }
  }
}
