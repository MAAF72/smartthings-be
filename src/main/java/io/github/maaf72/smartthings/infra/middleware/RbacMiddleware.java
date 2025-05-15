package io.github.maaf72.smartthings.infra.middleware;

import io.github.maaf72.smartthings.domain.user.entity.User.Role;
import io.github.maaf72.smartthings.infra.security.UserClaims;
import io.github.maaf72.smartthings.itf.AppMiddlewareItf;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import ratpack.core.handling.Context;

@ApplicationScoped
@RequiredArgsConstructor
public class RbacMiddleware implements AppMiddlewareItf {
  private final Role accessRole;

  @Override
  public void handle(Context ctx) throws Exception {
    UserClaims userClaims = ctx.maybeGet(UserClaims.class).orElse(null);

    if (userClaims == null) {
      ctx.getResponse().status(401).send("Unauthorized");

      return;
    }

    if (!userClaims.getRole().equals(accessRole)) {
      ctx.getResponse().status(403).send("Forbidden");
      
      return;
    }

    ctx.next();
  }
}
