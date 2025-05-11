package io.github.maaf72.smartthings.domain.device.handler;

import io.github.maaf72.smartthings.domain.device.usecase.DeviceUsecase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import ratpack.core.handling.Context;
import ratpack.core.handling.Handler;

@ApplicationScoped
@RequiredArgsConstructor
public class TodoHandler implements Handler {
  
  @Inject
  DeviceUsecase deviceUsecase;

  @Override
  public void handle(Context ctx) throws Exception {
    deviceUsecase.findByID(null);
    ctx.render("todo");
  }
}
