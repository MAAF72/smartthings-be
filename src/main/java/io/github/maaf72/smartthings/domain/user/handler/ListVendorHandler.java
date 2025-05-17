package io.github.maaf72.smartthings.domain.user.handler;

import java.util.List;

import io.github.maaf72.smartthings.domain.common.dto.PaginationRequest;
import io.github.maaf72.smartthings.domain.common.dto.PaginationResponse;
import io.github.maaf72.smartthings.domain.user.dto.VendorResponse;
import io.github.maaf72.smartthings.domain.user.dto.VendorWithSummaryResponse;
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
  path = "/admin/vendors", 
  operation = @Operation(
    method = "GET",
    tags = "Admin", 
    operationId = "ListVendor",
    summary = "List Vendor",
    description = "List Vendor",
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
        content = @Content(mediaType = "application/json", schema = @Schema(allOf = {PaginationResponse.class, VendorResponse.class}))
      )
    }
  )
)
public class ListVendorHandler implements Handler {
  
  @Inject
  UserUsecase userUsecase;

  @Override
  public void handle(Context ctx) throws Exception {
    UserClaims userClaims = ctx.get(UserClaims.class);

    PaginationRequest page = PaginationRequest.of(
      ctx.getRequest().getQueryParams().get("page"),
      ctx.getRequest().getQueryParams().get("size")
    );

    List<UserWithTotalRegisteredDevices> listVendor = userUsecase.listVendorWithTotalRegisteredDevices(userClaims.getId(), userClaims.getRole(), page);

    long totalVendor = userUsecase.countVendors();

    ctx.render(Jackson.json(PaginationResponse.of(
      true,
      "vendors retrieved",
      listVendor.stream().map(vendor -> {
        VendorWithSummaryResponse x = CustomObjectMapper.getObjectMapper().convertValue(vendor.getUser(), VendorWithSummaryResponse.class);
        x.setTotalRegisteredDevices(vendor.getTotalRegisteredDevices());
        return x;
      }).toList(),
      totalVendor,
      page
    )));
  }
}
