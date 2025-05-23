package io.github.maaf72.smartthings.domain.user.handler;

import java.util.UUID;

import io.github.maaf72.smartthings.domain.common.dto.BaseResponse;
import io.github.maaf72.smartthings.domain.user.dto.VendorResponse;
import io.github.maaf72.smartthings.domain.user.entity.User;
import io.github.maaf72.smartthings.domain.user.usecase.UserUsecase;
import io.github.maaf72.smartthings.infra.mapper.CustomObjectMapper;
import io.github.maaf72.smartthings.infra.oas.annotation.ApiDoc;
import io.github.maaf72.smartthings.infra.security.UserClaims;
import io.opentelemetry.instrumentation.annotations.WithSpan;
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
  path = "/admin/vendors/{id}", 
  operation = @Operation(
    method = "GET",
    tags = "Admin", 
    operationId = "GetVendor",
    summary = "Get Vendor",
    description = "Get Vendor",
    parameters = {
      @Parameter(
        name = "id", description = "vendor id", required = true, in = ParameterIn.PATH,
        schema = @Schema(type = "string", format = "uuid")
      ),
    },
    responses = {
      @ApiResponse(
        responseCode = "200", 
        description = "success response", 
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetVendorHandler.GetVendorResponse.class))
      )
    }
  )
)
public class GetVendorHandler implements Handler {
  
  @Inject
  UserUsecase userUsecase;

  @WithSpan
  public void handle(Context ctx) throws Exception {
    UserClaims userClaims = ctx.get(UserClaims.class);

    UUID userId = UUID.fromString(ctx.getPathTokens().get("id"));

    Promise.async(downstream -> 
      userUsecase.getUser(userClaims.getId(), userClaims.getRole(), userId).subscribe().with(
        user -> downstream.success(Jackson.json(new GetVendorResponse(user))),
        failure -> downstream.error(failure)
      )
    ).then(ctx::render);
  }

  class GetVendorResponse extends BaseResponse<VendorResponse> {
    GetVendorResponse(User user) {
      super(
        true,
        "vendor retrieved",
        CustomObjectMapper.getObjectMapper().convertValue(user, VendorResponse.class)
      );
    }
  }
}
