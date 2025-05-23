package io.github.maaf72.smartthings.domain.device.handler;

import java.util.List;

import io.github.maaf72.smartthings.domain.common.dto.PaginationRequest;
import io.github.maaf72.smartthings.domain.common.dto.PaginationResponse;
import io.github.maaf72.smartthings.domain.device.dto.DeviceAsUserResponse;
import io.github.maaf72.smartthings.domain.device.entity.Device;
import io.github.maaf72.smartthings.domain.device.usecase.DeviceUsecase;
import io.github.maaf72.smartthings.infra.mapper.CustomObjectMapper;
import io.github.maaf72.smartthings.infra.oas.annotation.ApiDoc;
import io.github.maaf72.smartthings.infra.security.UserClaims;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.smallrye.mutiny.Uni;
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
  path = "/devices", 
  operation = @Operation(
    method = "GET",
    tags = "User", 
    operationId = "ListAvailableDevice",
    summary = "List Available Device",
    description = "List Available Device",
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
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ListAvailableVendorDeviceHandler.ListAvailableDeviceResponse.class))
      )
    }
  )
)
public class ListAvailableVendorDeviceHandler implements Handler {
  
  @Inject
  DeviceUsecase deviceUsecase;

  @WithSpan
  public void handle(Context ctx) throws Exception {
    UserClaims userClaims = ctx.get(UserClaims.class);

    PaginationRequest page = PaginationRequest.of(
      ctx.getRequest().getQueryParams().get("page"),
      ctx.getRequest().getQueryParams().get("size")
    );

    Promise.async(downstream -> 
      Uni.combine().all().unis(
        deviceUsecase.listAvailableDevice(userClaims.getId(), page),
        deviceUsecase.countAvailableDevice()
      ).asTuple().subscribe().with(
        data -> downstream.success(Jackson.json(new ListAvailableDeviceResponse(data.getItem1(), data.getItem2(), page))),
        failure -> downstream.error(failure)
      )
    ).then(ctx::render);
  }

  class ListAvailableDeviceResponse extends PaginationResponse<DeviceAsUserResponse> {
    ListAvailableDeviceResponse(List<Device> listAvailableDevice, long totalAvailableDevice, PaginationRequest page) {
      super(
        true, 
        "available devices retrieved", 
        listAvailableDevice.stream().map(device ->  CustomObjectMapper.getObjectMapper().convertValue(device, DeviceAsUserResponse.class)).toList(),
        page.page, 
        page.size, 
        totalAvailableDevice
      );
    }
  }
}
