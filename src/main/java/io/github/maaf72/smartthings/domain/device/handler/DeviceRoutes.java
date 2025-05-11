package io.github.maaf72.smartthings.domain.device.handler;

import io.github.maaf72.smartthings.itf.AppRoutesItf;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import ratpack.core.handling.Chain;


@ApplicationScoped
@Slf4j
public class DeviceRoutes implements AppRoutesItf {
  public static final String PREFIX = "device";

  @Inject
  TodoHandler todoHandler;

  @Override
  public void Routes(Chain chain) {
    try {
      chain.prefix(PREFIX, x -> x
        .post("todo", todoHandler)
      );
    } catch (Exception e) {
      log.error("Error registering routes", e);
    }
  }
}
