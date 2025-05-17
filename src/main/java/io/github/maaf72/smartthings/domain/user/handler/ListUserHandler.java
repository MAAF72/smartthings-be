package io.github.maaf72.smartthings.domain.user.handler;

import java.util.List;

import io.github.maaf72.smartthings.domain.common.dto.PaginationRequest;
import io.github.maaf72.smartthings.domain.common.dto.PaginationResponse;
import io.github.maaf72.smartthings.domain.user.dto.UserResponse;
import io.github.maaf72.smartthings.domain.user.dto.UserWithSummaryResponse;
import io.github.maaf72.smartthings.domain.user.entity.UserWithTotalRegisteredDevices;
import io.github.maaf72.smartthings.domain.user.usecase.UserUsecase;
import io.github.maaf72.smartthings.infra.mapper.CustomObjectMapper;
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

@ApplicationScoped
@RequiredArgsConstructor
@ApiDoc(
  path = "/admin/users", 
  operation = @Operation(
    method = "GET",
    tags = "Admin", 
    operationId = "ListUser",
    summary = "List User",
    description = "List User",
    parameters = {
      @Parameter(
        name = "page", description = "page", required = false, in = ParameterIn.QUERY,
        schema = @Schema(type = "integer")
      ),
      @Parameter(
        name = "size", description = "size", required = false, in = ParameterIn.QUERY,
        schema = @Schema(type = "integer")
      ),
    },
    responses = {
      @ApiResponse(
        responseCode = "200", 
        description = "success response", 
        content = @Content(mediaType = "application/json", schema = @Schema(allOf = {PaginationResponse.class, UserResponse.class}))
      )
    }
  )
)
public class ListUserHandler implements Handler {
  
  @Inject
  UserUsecase userUsecase;

  @Override
  public void handle(Context ctx) throws Exception {
    UserClaims userClaims = ctx.get(UserClaims.class);

    PaginationRequest page = PaginationRequest.of(
      ctx.getRequest().getQueryParams().get("page"),
      ctx.getRequest().getQueryParams().get("size")
    );

    List<UserWithTotalRegisteredDevices> listUser = userUsecase.listUserWithTotalRegisteredDevices(userClaims.getId(), userClaims.getRole(), page);

    long totalUser = userUsecase.countUsers();

    ctx.render(Jackson.json(PaginationResponse.of(
      true,
      "users retrieved",
      listUser.stream().map(user -> {
        UserWithSummaryResponse x = CustomObjectMapper.getObjectMapper().convertValue(user.getUser(), UserWithSummaryResponse.class);
        x.setTotalRegisteredDevices(user.getTotalRegisteredDevices());
        return x;
      }).toList(),
      totalUser,
      page
    )));
  }
}
