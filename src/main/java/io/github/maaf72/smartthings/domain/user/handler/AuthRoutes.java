package io.github.maaf72.smartthings.domain.user.handler;

import io.github.maaf72.smartthings.itf.AppRoutesItf;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import ratpack.core.handling.Chain;


@ApplicationScoped
@Slf4j
public class AuthRoutes implements AppRoutesItf {
  public static final String PREFIX = "auth";

  @Inject
  RegisterHandler registerHandler;

  @Inject
  LoginHandler loginHandler;

  @Override
  public void Routes(Chain chain) {
    try {
      chain.prefix(PREFIX, x -> x
          .post("register", registerHandler)
          .post("login", loginHandler));
    } catch (Exception e) {
      log.error("Error registering routes", e);
    }
  }
}
