package io.github.maaf72.smartthings.domain.device.handler;

import java.util.UUID;

import io.github.maaf72.smartthings.domain.common.dto.BaseResponse;
import io.github.maaf72.smartthings.domain.device.usecase.DeviceUsecase;
import io.github.maaf72.smartthings.infra.security.UserClaims;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import ratpack.core.handling.Context;
import ratpack.core.handling.Handler;
import ratpack.core.jackson.Jackson;

@ApplicationScoped
@RequiredArgsConstructor
public class UnregisterUserDeviceHandler implements Handler {
  
  @Inject
  DeviceUsecase deviceUsecase;

  @Override
  public void handle(Context ctx) throws Exception {
    UserClaims userClaims = ctx.get(UserClaims.class);

    UUID deviceId = UUID.fromString(ctx.getPathTokens().get("id"));

    deviceUsecase.unregisterDevice(userClaims.getId(), userClaims.getRole(), deviceId);

    ctx.render(Jackson.json(BaseResponse.of(
      true,
      "device unregistered"
    )));
  }
}
