package io.github.maaf72.smartthings.domain.device.handler;

import io.github.maaf72.smartthings.domain.common.dto.BaseResponse;
import io.github.maaf72.smartthings.domain.device.dto.CreateDeviceRequest;
import io.github.maaf72.smartthings.domain.device.dto.DeviceResponse;
import io.github.maaf72.smartthings.domain.device.entity.Device;
import io.github.maaf72.smartthings.domain.device.usecase.DeviceUsecase;
import io.github.maaf72.smartthings.infra.mapper.CustomObjectMapper;
import io.github.maaf72.smartthings.infra.oas.annotation.ApiDoc;
import io.github.maaf72.smartthings.infra.security.UserClaims;
import io.github.maaf72.smartthings.infra.security.ValidationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import ratpack.core.handling.Context;
import ratpack.core.handling.Handler;
import ratpack.core.jackson.Jackson;

@ApplicationScoped
@RequiredArgsConstructor
@ApiDoc(
  path = "/vendor/devices", 
  operation = @Operation(
    method = "POST",
    tags = "Vendor", 
    operationId = "CreateVendorDevice",
    summary = "Create Vendor Device",
    description = "Create Vendor Device",
    requestBody = @RequestBody(
      required = true, 
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateDeviceRequest.class))
    ),
    responses = {
      @ApiResponse(
        responseCode = "200", 
        description = "success response", 
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeviceResponse.class))
      )
    }
  )
)
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
        CustomObjectMapper.getObjectMapper().convertValue(device, DeviceResponse.class)
      )));
    });
  }
}
