package io.github.maaf72.smartthings.domain.user.handler;

import io.github.maaf72.smartthings.domain.common.dto.BaseResponse;
import io.github.maaf72.smartthings.domain.user.dto.LoginRequest;
import io.github.maaf72.smartthings.domain.user.dto.TokenResponse;
import io.github.maaf72.smartthings.domain.user.usecase.UserUsecase;
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
  path = "/auth/login", 
  operation = @Operation(
    method = "POST",
    tags = "Auth", 
    operationId = "Login",
    summary = "Login",
    description = "Login",
    requestBody = @RequestBody(
      required = true, 
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginRequest.class))
    ),
    responses = {
      @ApiResponse(
        responseCode = "200", 
        description = "success response", 
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginHandler.LoginResponse.class))
      )
    }
  )
)
public class LoginHandler implements Handler {

  @Inject
  UserUsecase userUsecase;

  @WithSpan
  public void handle(Context ctx) {
    ctx.parse(Jackson.fromJson(LoginRequest.class)).then(request -> {
      ValidationUtil.validateOrThrow(request);

      Promise.async(downstream ->
        userUsecase.login(request).subscribe().with(
          token -> downstream.success(Jackson.json(new LoginResponse(token))),
          failure -> downstream.error(failure)
        )
      ).then(ctx::render);
    });
  }

  class LoginResponse extends BaseResponse<TokenResponse> {
    LoginResponse(String token) {
      super(
        true,
        "login success",
        new TokenResponse(token)
      );
    }
  }
}
