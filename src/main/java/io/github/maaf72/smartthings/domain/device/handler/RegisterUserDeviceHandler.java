package io.github.maaf72.smartthings.domain.device.handler;

import java.util.UUID;

import io.github.maaf72.smartthings.domain.common.dto.BaseResponse;
import io.github.maaf72.smartthings.domain.device.usecase.DeviceUsecase;
import io.github.maaf72.smartthings.infra.oas.annotation.ApiDoc;
import io.github.maaf72.smartthings.infra.security.UserClaims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
  path = "/users/me/device/{id}/register", 
  operation = @Operation(
    method = "POST",
    tags = "User", 
    operationId = "RegisterUserDevice",
    summary = "Register User Device",
    description = "Register User Device",
    parameters = {
      @Parameter(
        name = "id", description = "device id", required = true, in = ParameterIn.PATH,
        schema = @Schema(type = "string", format = "uuid")
      ),
    },
    responses = {
      @ApiResponse(
        responseCode = "200", 
        description = "success response", 
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))
      )
    }
  )
)
public class RegisterUserDeviceHandler implements Handler {
  
  @Inject
  DeviceUsecase deviceUsecase;

  @Override
  public void handle(Context ctx) throws Exception {
    UserClaims userClaims = ctx.get(UserClaims.class);

    UUID deviceId = UUID.fromString(ctx.getPathTokens().get("id"));

    Promise.async(downstream ->
      deviceUsecase.registerDevice(userClaims.getId(), userClaims.getRole(), deviceId).subscribe().with(
        _ -> downstream.success(Jackson.json(BaseResponse.of(true, "device registered"))),
        failure -> downstream.error(failure)
      )
    ).then(ctx::render);
  }
}
