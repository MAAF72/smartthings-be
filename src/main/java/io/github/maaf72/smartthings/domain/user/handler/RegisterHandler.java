package io.github.maaf72.smartthings.domain.user.handler;

import io.github.maaf72.smartthings.domain.user.usecase.UserUsecase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import ratpack.core.handling.Context;
import ratpack.core.handling.Handler;

@ApplicationScoped
@RequiredArgsConstructor
public class RegisterHandler implements Handler {

  @Inject
  UserUsecase userUsecase;

  @Override
  public void handle(Context ctx) throws Exception {
    ctx.render("register");
  }

}
