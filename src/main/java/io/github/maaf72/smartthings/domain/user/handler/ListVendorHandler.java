package io.github.maaf72.smartthings.domain.user.handler;

import java.util.List;

import io.github.maaf72.smartthings.domain.common.dto.PaginationRequest;
import io.github.maaf72.smartthings.domain.common.dto.PaginationResponse;
import io.github.maaf72.smartthings.domain.user.entity.UserWithTotalRegisteredDevices;
import io.github.maaf72.smartthings.domain.user.usecase.UserUsecase;
import io.github.maaf72.smartthings.infra.security.UserClaims;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import ratpack.core.handling.Context;
import ratpack.core.handling.Handler;
import ratpack.core.jackson.Jackson;

@ApplicationScoped
@RequiredArgsConstructor
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
      listVendor,
      totalVendor,
      page
    )));
  }
}
