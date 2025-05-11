package io.github.maaf72.smartthings.domain.user.handler;

import io.github.maaf72.smartthings.itf.AppRoutesItf;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import ratpack.core.handling.Chain;


@ApplicationScoped
@Slf4j
public class UserRoutes implements AppRoutesItf {
  public static final String PREFIX = "user";

  @Inject
  UserDeviceListHandler userDeviceListHandler;

  @Override
  public void Routes(Chain chain) {
    try {
      chain.prefix(PREFIX, x -> x
        .get("device", userDeviceListHandler)
      );
    } catch (Exception e) {
      log.error("Error registering routes", e);
    }
  }
}
