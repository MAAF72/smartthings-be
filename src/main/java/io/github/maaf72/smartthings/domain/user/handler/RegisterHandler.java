package io.github.maaf72.smartthings.domain.user.handler;

import io.github.maaf72.smartthings.domain.common.dto.BaseResponse;
import io.github.maaf72.smartthings.domain.user.dto.RegisterRequest;
import io.github.maaf72.smartthings.domain.user.dto.RegisterResponse;
import io.github.maaf72.smartthings.domain.user.entity.User;
import io.github.maaf72.smartthings.domain.user.usecase.UserUsecase;
import io.github.maaf72.smartthings.infra.security.ValidationUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ratpack.core.handling.Context;
import ratpack.core.handling.Handler;
import ratpack.core.jackson.Jackson;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class RegisterHandler implements Handler {

  @Inject
  UserUsecase userUsecase;

  @Override
  public void handle(Context ctx) throws Exception {
    ctx.parse(Jackson.fromJson(RegisterRequest.class)).then(request -> {
      ValidationUtil.validateOrThrow(request);

      User user = userUsecase.register(request);

      ctx.render(Jackson.json(BaseResponse.of(
        true,
        "register success",
        new RegisterResponse(user.getId(), user.getEmail(), user.getRole())
      )));
    });
  }
}
