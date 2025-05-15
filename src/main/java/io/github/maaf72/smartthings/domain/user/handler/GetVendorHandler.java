package io.github.maaf72.smartthings.domain.user.handler;

import java.util.UUID;

import io.github.maaf72.smartthings.domain.common.dto.BaseResponse;
import io.github.maaf72.smartthings.domain.user.entity.User;
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
public class GetVendorHandler implements Handler {
  
  @Inject
  UserUsecase userUsecase;

  @Override
  public void handle(Context ctx) throws Exception {
    UserClaims userClaims = ctx.get(UserClaims.class);

    UUID userId = UUID.fromString(ctx.getPathTokens().get("id"));

    User user = userUsecase.getUser(userClaims.getId(), userClaims.getRole(), userId);

    ctx.render(Jackson.json(BaseResponse.of(
      true,
      "vendor retrieved",
      user
    )));
  }
}
