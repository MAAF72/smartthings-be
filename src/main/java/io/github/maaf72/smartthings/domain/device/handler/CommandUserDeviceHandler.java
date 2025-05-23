package io.github.maaf72.smartthings.domain.device.handler;

import java.util.UUID;

import io.github.maaf72.smartthings.domain.common.dto.BaseResponse;
import io.github.maaf72.smartthings.domain.device.dto.CommandUserDeviceRequest;
import io.github.maaf72.smartthings.domain.device.dto.DeviceResponse;
import io.github.maaf72.smartthings.domain.device.dto.UpdateVendorDeviceRequest;
import io.github.maaf72.smartthings.domain.device.entity.Device;
import io.github.maaf72.smartthings.domain.device.usecase.DeviceUsecase;
import io.github.maaf72.smartthings.infra.mapper.CustomObjectMapper;
import io.github.maaf72.smartthings.infra.oas.annotation.ApiDoc;
import io.github.maaf72.smartthings.infra.security.UserClaims;
import io.github.maaf72.smartthings.infra.security.ValidationUtil;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
  path = "/users/me/devices/{id}/commands", 
  operation = @Operation(
    method = "PUT",
    tags = "User", 
    operationId = "CommandUserDevice",
    summary = "Command User Device",
    description = "Command User Device",
    parameters = {
      @Parameter(
        name = "id", description = "device id", required = true, in = ParameterIn.PATH,
        schema = @Schema(type = "string", format = "uuid")
      ),
    },
    requestBody = @RequestBody(
      required = true, 
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommandUserDeviceRequest.class))
    ),
    responses = {
      @ApiResponse(
        responseCode = "200", 
        description = "success response", 
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommandUserDeviceHandler.CommandUserDeviceResponse.class))
      )
    }
  )
)
public class CommandUserDeviceHandler implements Handler {
  
  @Inject
  DeviceUsecase deviceUsecase;

  @WithSpan
  public void handle(Context ctx) throws Exception {
    ctx.parse(Jackson.fromJson(CommandUserDeviceRequest.class)).then(request -> {
      ValidationUtil.validateOrThrow(request);

      UserClaims userClaims = ctx.get(UserClaims.class);

      UUID deviceId = UUID.fromString(ctx.getPathTokens().get("id"));

      UpdateVendorDeviceRequest updateVendorDeviceRequest = new UpdateVendorDeviceRequest();
      updateVendorDeviceRequest.setValue(request.getValue());

      Promise.async(downstream ->
        deviceUsecase.updateDevice(userClaims.getId(), userClaims.getRole(), updateVendorDeviceRequest, deviceId).subscribe().with(
          device -> downstream.success(Jackson.json(new CommandUserDeviceResponse(device))),
          failure -> downstream.error(failure)
        )
      ).then(ctx::render);
    });
  }

  class CommandUserDeviceResponse extends BaseResponse<DeviceResponse> {
    CommandUserDeviceResponse(Device device) {
      super(
        true,
        "device commanded",
        CustomObjectMapper.getObjectMapper().convertValue(device, DeviceResponse.class)
      );
    }
  }
}
