package io.github.maaf72.smartthings.domain.user.handler;

import io.github.maaf72.smartthings.domain.common.dto.BaseResponse;
import io.github.maaf72.smartthings.domain.user.dto.ProfileResponse;
import io.github.maaf72.smartthings.domain.user.dto.RegisterRequest;
import io.github.maaf72.smartthings.domain.user.entity.User;
import io.github.maaf72.smartthings.domain.user.usecase.UserUsecase;
import io.github.maaf72.smartthings.infra.mapper.CustomObjectMapper;
import io.github.maaf72.smartthings.infra.oas.annotation.ApiDoc;
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
  path = "/auth/register", 
  operation = @Operation(
    method = "POST",
    tags = "Auth", 
    operationId = "Register",
    summary = "Register",
    description = "Register",
    requestBody = @RequestBody(
      required = true, 
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegisterRequest.class))
    ),
    responses = {
      @ApiResponse(
        responseCode = "200", 
        description = "success response", 
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegisterHandler.RegisterResponse.class))
      )
    }
  )
)
public class RegisterHandler implements Handler {

  @Inject
  UserUsecase userUsecase;

  @WithSpan
  public void handle(Context ctx) throws Exception {
    ctx.parse(Jackson.fromJson(RegisterRequest.class)).then(request -> {
      ValidationUtil.validateOrThrow(request);

      Promise.async(downstream ->
        userUsecase.register(request).subscribe().with(
          user -> downstream.success(Jackson.json(new RegisterResponse(user))),
          failure -> downstream.error(failure)
        )
      ).then(ctx::render);
    });
  }

  class RegisterResponse extends BaseResponse<ProfileResponse> {
    RegisterResponse(User user) {
      super(
        true,
        "register success",
        CustomObjectMapper.getObjectMapper().convertValue(user, ProfileResponse.class)
      );
    }
  }
}

