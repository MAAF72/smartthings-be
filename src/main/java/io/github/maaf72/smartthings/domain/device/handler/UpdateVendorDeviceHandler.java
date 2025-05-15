package io.github.maaf72.smartthings.domain.device.handler;

import java.util.UUID;

import io.github.maaf72.smartthings.domain.common.dto.BaseResponse;
import io.github.maaf72.smartthings.domain.device.dto.UpdateDeviceRequest;
import io.github.maaf72.smartthings.domain.device.entity.Device;
import io.github.maaf72.smartthings.domain.device.usecase.DeviceUsecase;
import io.github.maaf72.smartthings.infra.security.UserClaims;
import io.github.maaf72.smartthings.infra.security.ValidationUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import ratpack.core.handling.Context;
import ratpack.core.handling.Handler;
import ratpack.core.jackson.Jackson;

@ApplicationScoped
@RequiredArgsConstructor
public class UpdateVendorDeviceHandler implements Handler {
  
  @Inject
  DeviceUsecase deviceUsecase;

  @Override
  public void handle(Context ctx) throws Exception {
    ctx.parse(Jackson.fromJson(UpdateDeviceRequest.class)).then(request -> {
      ValidationUtil.validateOrThrow(request);

      UserClaims userClaims = ctx.get(UserClaims.class);

      UUID deviceId = UUID.fromString(ctx.getPathTokens().get("id"));

      Device device = deviceUsecase.updateDevice(userClaims.getId(), userClaims.getRole(), request, deviceId);

      ctx.render(Jackson.json(BaseResponse.of(
        true,
        "device updated",
        device
      )));
    });
  }
}
