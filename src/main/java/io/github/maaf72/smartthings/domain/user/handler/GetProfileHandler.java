package io.github.maaf72.smartthings.domain.user.handler;

import io.github.maaf72.smartthings.domain.common.dto.BaseResponse;
import io.github.maaf72.smartthings.domain.user.dto.ProfileResponse;
import io.github.maaf72.smartthings.domain.user.entity.User;
import io.github.maaf72.smartthings.domain.user.usecase.UserUsecase;
import io.github.maaf72.smartthings.infra.mapper.CustomObjectMapper;
import io.github.maaf72.smartthings.infra.oas.annotation.ApiDoc;
import io.github.maaf72.smartthings.infra.security.UserClaims;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.swagger.v3.oas.annotations.Operation;
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
  path = "/users/me", 
  operation = @Operation(
    method = "GET",
    tags = "Profile", 
    operationId = "GetProfile",
    summary = "Get Profile",
    responses = {
      @ApiResponse(
        responseCode = "200", 
        description = "success response", 
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetProfileHandler.GetProfileResponse.class))
      )
    }
  )
)
public class GetProfileHandler implements Handler {
  
  @Inject
  UserUsecase userUsecase;

  @WithSpan
  public void handle(Context ctx) throws Exception {
    UserClaims userClaims = ctx.get(UserClaims.class);
    
    Promise.async(downstream ->
      userUsecase.getUser(userClaims.getId(), userClaims.getRole(), userClaims.getId()).subscribe().with(
        user -> downstream.success(Jackson.json(new GetProfileResponse(user))),
        failure -> downstream.error(failure)
      )
    ).then(ctx::render);
  }

  class GetProfileResponse extends BaseResponse<ProfileResponse> {
    GetProfileResponse(User user) {
      super(
        true,
        "profile retrieved",
        CustomObjectMapper.getObjectMapper().convertValue(user, ProfileResponse.class)
      );
    }
  }
}
