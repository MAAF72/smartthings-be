package io.github.maaf72.smartthings.domain.device.handler;

import io.github.maaf72.smartthings.domain.common.dto.BaseResponse;
import io.github.maaf72.smartthings.domain.device.dto.CreateVendorDeviceRequest;
import io.github.maaf72.smartthings.domain.device.dto.DeviceResponse;
import io.github.maaf72.smartthings.domain.device.entity.Device;
import io.github.maaf72.smartthings.domain.device.usecase.DeviceUsecase;
import io.github.maaf72.smartthings.infra.mapper.CustomObjectMapper;
import io.github.maaf72.smartthings.infra.oas.annotation.ApiDoc;
import io.github.maaf72.smartthings.infra.security.UserClaims;
import io.github.maaf72.smartthings.infra.security.ValidationUtil;
import io.opentelemetry.instrumentation.annotations.WithSpan;
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
import ratpack.exec.Promise;

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
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateVendorDeviceRequest.class))
    ),
    responses = {
      @ApiResponse(
        responseCode = "200", 
        description = "success response", 
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateVendorDeviceHandler.CreateVendorDeviceResponse.class))
      )
    }
  )
)
public class CreateVendorDeviceHandler implements Handler {
  
  @Inject
  DeviceUsecase deviceUsecase;

  @WithSpan
  public void handle(Context ctx) throws Exception {
    ctx.parse(Jackson.fromJson(CreateVendorDeviceRequest.class)).then(request -> {
      ValidationUtil.validateOrThrow(request);

      UserClaims userClaims = ctx.get(UserClaims.class);

      Promise.async(downstream ->
        deviceUsecase.createDevice(userClaims.getId(), userClaims.getRole(), request).subscribe().with(
          device -> downstream.success(Jackson.json(new CreateVendorDeviceResponse(device))),
          failure -> downstream.error(failure)
        )
      ).then(ctx::render);
    });
  }

  class CreateVendorDeviceResponse extends BaseResponse<DeviceResponse> {
    CreateVendorDeviceResponse(Device device) {
      super(
        true,
        "device created",
        CustomObjectMapper.getObjectMapper().convertValue(device, DeviceResponse.class)
      );
    }
  }
}
