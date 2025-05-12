package io.github.maaf72.smartthings.domain.device.handler;

import io.github.maaf72.smartthings.domain.common.dto.BaseResponse;
import io.github.maaf72.smartthings.domain.device.dto.CreateDeviceRequest;
import io.github.maaf72.smartthings.domain.device.entity.Device;
import io.github.maaf72.smartthings.domain.device.usecase.DeviceUsecase;
import io.github.maaf72.smartthings.infra.security.UserClaims;
import io.github.maaf72.smartthings.infra.security.ValidationUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ratpack.core.handling.Context;
import ratpack.core.handling.Handler;
import ratpack.core.jackson.Jackson;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class CreateVendorDeviceHandler implements Handler {
  
  @Inject
  DeviceUsecase deviceUsecase;

  @Override
  public void handle(Context ctx) throws Exception {
    ctx.parse(Jackson.fromJson(CreateDeviceRequest.class)).then(request -> {
      ValidationUtil.validateOrThrow(request);

      UserClaims userClaims = ctx.get(UserClaims.class);

      Device device = deviceUsecase.createDevice(userClaims.getId(), userClaims.getRole(), request);
      
      ctx.render(Jackson.json(BaseResponse.of(
        true,
        "device created",
        device
      )));
    });
  }
}
