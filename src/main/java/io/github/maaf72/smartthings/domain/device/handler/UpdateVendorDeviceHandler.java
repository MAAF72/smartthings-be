package io.github.maaf72.smartthings.domain.device.handler;

import java.util.UUID;

import io.github.maaf72.smartthings.domain.common.dto.BaseResponse;
import io.github.maaf72.smartthings.domain.device.dto.DeviceResponse;
import io.github.maaf72.smartthings.domain.device.dto.UpdateVendorDeviceRequest;
import io.github.maaf72.smartthings.domain.device.entity.Device;
import io.github.maaf72.smartthings.domain.device.usecase.DeviceUsecase;
import io.github.maaf72.smartthings.infra.mapper.CustomObjectMapper;
import io.github.maaf72.smartthings.infra.oas.annotation.ApiDoc;
import io.github.maaf72.smartthings.infra.security.UserClaims;
import io.github.maaf72.smartthings.infra.security.ValidationUtil;
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

@ApplicationScoped
@RequiredArgsConstructor
@ApiDoc(
  path = "/vendor/devices/{id}", 
  operation = @Operation(
    method = "PUT",
    tags = "Vendor", 
    operationId = "UpdateVendorDevice",
    summary = "Update Vendor Device",
    description = "Update Vendor Device",
    parameters = {
      @Parameter(
        name = "id", description = "device id", required = true, in = ParameterIn.PATH,
        schema = @Schema(type = "string", format = "uuid")
      ),
    },
    requestBody = @RequestBody(
      required = true, 
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpdateVendorDeviceRequest.class))
    ),
    responses = {
      @ApiResponse(
        responseCode = "200", 
        description = "success response", 
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpdateVendorDeviceHandler.UpdateVendorDeviceResponse.class))
      )
    }
  )
)
public class UpdateVendorDeviceHandler implements Handler {
  
  @Inject
  DeviceUsecase deviceUsecase;

  @Override
  public void handle(Context ctx) throws Exception {
    ctx.parse(Jackson.fromJson(UpdateVendorDeviceRequest.class)).then(request -> {
      ValidationUtil.validateOrThrow(request);

      UserClaims userClaims = ctx.get(UserClaims.class);

      UUID deviceId = UUID.fromString(ctx.getPathTokens().get("id"));

      Device device = deviceUsecase.updateDevice(userClaims.getId(), userClaims.getRole(), request, deviceId);

      ctx.render(Jackson.json(new UpdateVendorDeviceResponse(device)));
    });
  }

  class UpdateVendorDeviceResponse extends BaseResponse<DeviceResponse> {
    UpdateVendorDeviceResponse(Device device) {
      super(
        true,
        "device updated",
        CustomObjectMapper.getObjectMapper().convertValue(device, DeviceResponse.class)
      );
    }
  }
}
